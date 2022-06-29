package com.example.mileage.event.service;

import com.example.mileage.common.Action;
import com.example.mileage.common.MessageSourceToStr;
import com.example.mileage.common.Point;
import com.example.mileage.common.exception.CustomException;
import com.example.mileage.common.exception.CustomExceptionEnum;
import com.example.mileage.event.dto.UserPointResDto;
import com.example.mileage.event.dto.PointReqDto;
import com.example.mileage.event.entity.*;
import com.example.mileage.event.repository.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * Event Service Class
 */
@Service
public class EventService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PhotoRepository photoRepository;

    public EventService(ReviewRepository reviewRepository, UserRepository userRepository, PlaceRepository placeRepository, PointHistoryRepository pointHistoryRepository, PhotoRepository photoRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.placeRepository = placeRepository;
        this.pointHistoryRepository = pointHistoryRepository;
        this.photoRepository = photoRepository;
    }

    /**
     * 포인트 적립 Service
     * @param pointReqDto requestBody Dto
     * @throws Exception
     */
    public void accumulatePoint(PointReqDto pointReqDto) throws Exception {
        //  1. 유저, 장소 데이터 여부 체크 후 없으면 Insert
        userAndPlaceSetUp(pointReqDto);

        //  2. Action값으로  비즈니스 로직 분기 처리
        Action action = Action.valueOf(pointReqDto.getAction());
        switch (action) {
            case ADD:
                addEvent(pointReqDto);
                break;
            case MOD:
                modEvent(pointReqDto);
                break;
            case DELETE:
                deleteEvent(pointReqDto);
                break;
            default:
                throw new CustomException.Builder()
                        .code(CustomExceptionEnum.BUSINESS_EXCEPTION.getCode())
                        .message("Not Valid Action Value")
                        .build();
        }
    }

    /**
     * 요청들어온 RequestBody 값으로 유저와 장소 등록하기
     * 데이터베이스에 데이터가 없으므로 들어오는 값이 유효한 UUID면 DB에 적재
     *
     * @param pointReqDto requestBody Dto
     */
    private void userAndPlaceSetUp(PointReqDto pointReqDto) {
        //  1-1. UserId로 User 데이터 있는지 Check
        UUID userId = UUID.fromString(pointReqDto.getUserId());
        Optional<User> optionalUser = userRepository.findById(userId);
        //  1-2. User 데이터 없으면 Save
        optionalUser.ifPresentOrElse(
                pointReqDto::setUser
                ,() -> {
                    User saveUser =
                            User.builder()
                                    .userId(userId)
                                    .build();
                    pointReqDto.setUser(userRepository.save(saveUser));
                }
        );
        //  2-1. PlaceId로 Place 데이터 있는지 Check
        UUID placeId = UUID.fromString(pointReqDto.getPlaceId());
        Optional<Place> optionalPlace = placeRepository.findById(placeId);
        //  2-2. Place 데이터 없으면 Save
        optionalPlace.ifPresentOrElse(
                pointReqDto::setPlace
                ,() -> {
                    Place savePlace =
                            Place.builder()
                                    .placeId(placeId)
                                    .build();
                    pointReqDto.setPlace(placeRepository.save(savePlace));
                }
        );
    }

    /**
     * 리뷰 작성 이벤트
     * @param pointReqDto requestBody Dto
     */
    private void addEvent(PointReqDto pointReqDto) throws Exception {
        //  0. 같은 리뷰아이디로 작성 할 시 & 동일한 사용자가 동일한 장소 작성 할 시 Exception
        //  0-1. 같은 리뷰아이디로 작성시
        UUID reviewId = UUID.fromString(pointReqDto.getReviewId());
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if(optionalReview.isPresent()) {
            throw new CustomException.Builder()
                    .code(CustomExceptionEnum.BUSINESS_EXCEPTION.getCode())
                    .message(MessageSourceToStr.DUPLICATE_REVIEW_ID)
                    .build();
        }
        // 0-2. 동일한 사용자가 동일한 장소 작성할시 Exception
        if(reviewRepository.findByUserAndPlace(pointReqDto.getUser(), pointReqDto.getPlace()).isPresent()) {
            throw new CustomException.Builder()
                    .code(CustomExceptionEnum.BUSINESS_EXCEPTION.getCode())
                    .message(MessageSourceToStr.ONLY_ONE_USER_PLACE)
                    .build();
        }

        //  1. content & imageIds Check
        String content = pointReqDto.getContent() == null || pointReqDto.getContent().strip().length() == 0 ? null : pointReqDto.getContent().strip();
        String[] imageIds = pointReqDto.getAttachedPhotoIds();

        //  2. Review 담기
        Review saveReview =
                Review.builder()
                        .reviewId(reviewId)
                        .content(content)
                        .user(pointReqDto.getUser())
                        .place(pointReqDto.getPlace())
                        .build();
        reviewRepository.save(saveReview);

        //  3. content 내용이 있을경우 PointHistory 1점 추가
        if(content != null) {
            PointHistory savePointHistory =
                    PointHistory.builder()
                            .historyId(UUID.randomUUID())
                            .userId(pointReqDto.getUser().getUserId())
                            .reviewId(reviewId)
                            .point(Point.ContentAdd.getScore())
                            .tag('C')
                            .reason(Point.ContentAdd.getReason())
                            .build();
            pointHistoryRepository.save(savePointHistory);
        }

        //  4. Image 있을경우 1점 추가
        if(imageIds != null && imageIds.length > 0) {
            //  4-1. Photo 테이블에 Insert
            Arrays.stream(imageIds).forEach(photoId -> {
                Photo savePhoto =
                        Photo.builder()
                                .photoId(UUID.fromString(photoId))
                                .review(saveReview)
                                .build();
                photoRepository.save(savePhoto);
            });
            //  4-2. PointHistory 1점 추가
            PointHistory savePointHistory =
                    PointHistory.builder()
                            .historyId(UUID.randomUUID())
                            .userId(pointReqDto.getUser().getUserId())
                            .reviewId(reviewId)
                            .point(Point.ImageAdd.getScore())
                            .tag('I')
                            .reason(Point.ImageAdd.getReason())
                            .build();
            pointHistoryRepository.save(savePointHistory);
        }

        //  5. 첫 리뷰일경우 1점 보너스 점수 추가
        List<Review> reviewList = reviewRepository.findAllByPlace(pointReqDto.getPlace());
        if(reviewList.size() == 1) {
            PointHistory savePointHistory =
                    PointHistory.builder()
                            .historyId(UUID.randomUUID())
                            .userId(pointReqDto.getUser().getUserId())
                            .reviewId(reviewId)
                            .point(Point.BonusAdd.getScore())
                            .tag('B')
                            .reason(Point.BonusAdd.getReason())
                            .build();
            pointHistoryRepository.save(savePointHistory);
        }
    }

    /**
     * 리뷰 수정 이벤트
     * @param pointReqDto requestBody Dto
     */
    private void modEvent(PointReqDto pointReqDto) throws Exception {
        //  0. 리뷰아이디 Check
        UUID reviewId = UUID.fromString(pointReqDto.getReviewId());
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        Review currentReview;       //  현재리뷰 Object

        //  0-1. 리뷰아이디 있을 시 작성자와 요청자 비교
        if(optionalReview.isPresent()) {
            currentReview = optionalReview.get();
            if(!currentReview.getUser().getUserId().equals(pointReqDto.getUser().getUserId())) {
                throw new CustomException.Builder()
                        .code(CustomExceptionEnum.BUSINESS_EXCEPTION.getCode())
                        .message(MessageSourceToStr.MOD_ONLY_WRITER)
                        .build();
            }
        }
        //  0-2. 리뷰아이디 없을시 Exception
        else {
            throw new CustomException.Builder()
                    .code(CustomExceptionEnum.BUSINESS_EXCEPTION.getCode())
                    .message(MessageSourceToStr.NOT_REGISTERED_REVIEW_ID)
                    .build();
        }

        //  1. content & imageIds Check
        boolean currentContent = currentReview.getContent() != null;
        String content = pointReqDto.getContent() == null || pointReqDto.getContent().strip().length() == 0 ? null : pointReqDto.getContent().strip();
        String[] imageIds = pointReqDto.getAttachedPhotoIds();

        //  2. Review 수정
        Review updateReview =
                Review.builder()
                        .reviewId(reviewId)
                        .user(pointReqDto.getUser())
                        .place(pointReqDto.getPlace())
                        .content(content)
                        .updateAt(new Timestamp(System.currentTimeMillis()))
                        .build();
        reviewRepository.save(updateReview);

        // 3. Content 변화에 따른 포인트 수정
        // 3-1. 기존 리뷰에 content가 있고 수정한 리뷰에 content가 없을 경우
        if(currentContent && content == null) {
            PointHistory pointHistory =
                    PointHistory.builder()
                            .historyId(UUID.randomUUID())
                            .reviewId(reviewId)
                            .userId(currentReview.getUser().getUserId())
                            .point(Point.ContentDelete.getScore())
                            .tag('C')
                            .reason(Point.ContentDelete.getReason())
                            .build();
            pointHistoryRepository.save(pointHistory);
        }
        // 3-2. 기존 리뷰에 content가 없고 수정한 리뷰에 content가 있을 경우
        else if(!currentContent && content != null) {
            PointHistory pointHistory =
                    PointHistory.builder()
                            .historyId(UUID.randomUUID())
                            .reviewId(reviewId)
                            .userId(currentReview.getUser().getUserId())
                            .point(Point.ContentAdd.getScore())
                            .tag('C')
                            .reason(Point.ContentAdd.getReason())
                            .build();
            pointHistoryRepository.save(pointHistory);
        }

        // 4. imageIds 변화에 따른 포인트 수정
        List<Photo> photoList = photoRepository.findAllByReview(currentReview);
        // 4-1. 기존 리뷰에 이미지가 있고 수정한 리뷰에 이미지가 없을 경우
        if(!photoList.isEmpty() && imageIds == null) {
            PointHistory pointHistory =
                    PointHistory.builder()
                            .historyId(UUID.randomUUID())
                            .reviewId(reviewId)
                            .userId(currentReview.getUser().getUserId())
                            .point(Point.ImageDelete.getScore())
                            .tag('I')
                            .reason(Point.ImageDelete.getReason())
                            .build();
            pointHistoryRepository.save(pointHistory);

            // 4-1-1. 포토 테이블에 기존 이미지들 삭제
            photoRepository.deleteAll(photoList);
        }
        // 4-2. 기존 리뷰에 이미지가 없고 수정한 리뷰에 이미지가 있을 경우
        else if(photoList.isEmpty() && (imageIds != null && imageIds.length > 0)) {
            PointHistory pointHistory =
                    PointHistory.builder()
                            .historyId(UUID.randomUUID())
                            .reviewId(reviewId)
                            .userId(currentReview.getUser().getUserId())
                            .point(Point.ImageAdd.getScore())
                            .tag('I')
                            .reason(Point.ImageAdd.getReason())
                            .build();
            pointHistoryRepository.save(pointHistory);

            // 4-2-1. 포토 테이블에 이미지 적재
            Arrays.stream(imageIds).forEach(photoId -> {
                Photo savePhoto =
                        Photo.builder()
                                .photoId(UUID.fromString(photoId))
                                .review(currentReview)
                                .build();
                photoRepository.save(savePhoto);
            });
        }
        // 4-3. 기존 리뷰에 이미지도 있고 수정한 리뷰에도 이미지가 있을 경우 -> 점수 수정은 없고 포토 테이블만 수정
        else if(!photoList.isEmpty() && imageIds.length > 0) {
            // 4-3-1. 포토 테이블에 기존 이미지들 삭제
            photoRepository.deleteAll(photoList);
            // 4-3-2. 포토 테이블에 새로운 이미지 적재
            Arrays.stream(imageIds).forEach(photoId -> {
                Photo savePhoto =
                        Photo.builder()
                                .photoId(UUID.fromString(photoId))
                                .review(currentReview)
                                .build();
                photoRepository.save(savePhoto);
            });
        }
    }

    /**
     * 리뷰 삭제 이벤트
     * @param pointReqDto requestBody Dto
     */
    private void deleteEvent(PointReqDto pointReqDto) throws Exception {
        //  0. 리뷰아이디 Check
        UUID reviewId = UUID.fromString(pointReqDto.getReviewId());
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        Review currentReview;       //  현재리뷰 Object

        //  0-1. 리뷰아이디 있을 시 작성자와 요청자 비교
        if(optionalReview.isPresent()) {
            currentReview = optionalReview.get();
            if(!currentReview.getUser().getUserId().equals(pointReqDto.getUser().getUserId())) {
                throw new CustomException.Builder()
                        .code(CustomExceptionEnum.BUSINESS_EXCEPTION.getCode())
                        .message(MessageSourceToStr.DELETE_ONLY_WRITER)
                        .build();
            }
        }
        //  0-2. 리뷰아이디 없을시 Exception
        else {
            throw new CustomException.Builder()
                    .code(CustomExceptionEnum.BUSINESS_EXCEPTION.getCode())
                    .message(MessageSourceToStr.NOT_REGISTERED_REVIEW_ID)
                    .build();
        }

        // 1. 해당 리뷰에 콘텐츠가 있을경우 포인트 회수
        if(currentReview.getContent() !=  null) {
            PointHistory pointHistory =
                    PointHistory.builder()
                            .historyId(UUID.randomUUID())
                            .reviewId(reviewId)
                            .userId(currentReview.getUser().getUserId())
                            .point(Point.ContentDelete.getScore())
                            .tag('C')
                            .reason(Point.ContentDelete.getReason())
                            .build();
            pointHistoryRepository.save(pointHistory);
        }

        //  2. 해당 리뷰에 이미지가 있을경우 Photo 테이블 삭제 및 포인트 회수
        List<Photo> photoList = photoRepository.findAllByReview(currentReview);
        if(!photoList.isEmpty()) {
            photoRepository.deleteAll(photoList);
            PointHistory pointHistory =
                    PointHistory.builder()
                            .historyId(UUID.randomUUID())
                            .reviewId(reviewId)
                            .userId(currentReview.getUser().getUserId())
                            .point(Point.ImageDelete.getScore())
                            .tag('I')
                            .reason(Point.ImageDelete.getReason())
                            .build();
            pointHistoryRepository.save(pointHistory);
        }

        // 3. 해당 리뷰가 장소의 첫 리뷰일경우 보너스 점수 회수(포인트 히스토리에서 해당 리뷰 아이디로 보너스 점수가 있을시 회수)
        List<PointHistory> pointHistories = pointHistoryRepository.findAllByReviewIdAndTag(reviewId, 'B');
        if(!pointHistories.isEmpty()) {
            PointHistory pointHistory =
                    PointHistory.builder()
                            .historyId(UUID.randomUUID())
                            .reviewId(reviewId)
                            .userId(currentReview.getUser().getUserId())
                            .point(Point.BonusDelete.getScore())
                            .tag('B')
                            .reason(Point.BonusDelete.getReason())
                            .build();
            pointHistoryRepository.save(pointHistory);
        }

        //  4. 리뷰 삭제
        reviewRepository.delete(currentReview);
    }


    /**
     * 현재 보유한 포인트 및 내역 조회 Service
     * @param userId 유저아이디(UUID)
     * @return
     */
    public UserPointResDto getUserPoint(String userId) {
        //  1. 포인트 History 조회
        List<PointHistory> pointHistories = pointHistoryRepository.findAllByUserIdOrderByCreatedAtDesc(UUID.fromString(userId));

        //  2. 조회한 값으로 PointDetail 만들기
        int totalPoint = 0;
        List<UserPointResDto.PointDetail> detailList = new ArrayList<>(pointHistories.size());
        for (PointHistory pointHistory : pointHistories) {
            detailList.add(UserPointResDto.PointDetail.builder()
                    .reviewId(pointHistory.getReviewId().toString())
                    .reason(pointHistory.getReason())
                    .point(pointHistory.getPoint())
                    .createdAt(pointHistory.getCreatedAt())
                    .build()
            );
            totalPoint += pointHistory.getPoint();
        }

        return UserPointResDto.builder()
                .pointDetails(detailList)
                .totalPoint(totalPoint)
                .build();
    }
}
