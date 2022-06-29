package com.example.mileage.config;

import org.springframework.beans.factory.config.YamlProcessor;
import org.springframework.core.CollectionFactory;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * yaml properties 파일 사용을 위한 클래스
 */
class YamlPropertiesProcessor extends YamlProcessor {
    /**
     * constructor
     *
     * @param resource resource
     * @throws IOException exception
     */
    public YamlPropertiesProcessor(Resource resource) throws IOException {
        if (!resource.exists()) {
            throw new FileNotFoundException();
        }
        this.setResources(resource);
    }

    public Properties createProperties() throws IOException {
        final Properties result = CollectionFactory.createStringAdaptingProperties();
        process((properties, map) -> result.putAll(properties));
        return result;
    }
}

public class YamlPropertySourceFactory implements PropertySourceFactory {
    private static final String YML_FILE_EXTENSION = ".yml";

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        String filename = resource.getResource().getFilename();
        if (filename != null && filename.endsWith(YML_FILE_EXTENSION)) {
            return name != null ? new YamlResourcePropertySource(name, resource) : new YamlResourcePropertySource(getNameForResource(resource.getResource()), resource);
        }
        return (name != null ? new ResourcePropertySource(name, resource) : new ResourcePropertySource(resource));
    }

    private String getNameForResource(Resource resource) {
        String name = resource.getDescription();
        if (!StringUtils.hasText(name)) {
            name = resource.getClass().getSimpleName() + "@" + System.identityHashCode(resource);
        }
        return name;
    }
}

class YamlResourcePropertySource extends PropertiesPropertySource {
    public YamlResourcePropertySource(String name, EncodedResource resource) throws IOException {
        super(name, new YamlPropertiesProcessor(resource.getResource()).createProperties());
    }
}
