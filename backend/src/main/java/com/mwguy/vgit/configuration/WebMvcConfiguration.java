package com.mwguy.vgit.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ResourceResolver resolver = new ReactResourceResolver();
        registry.addResourceHandler("/**")
                .resourceChain(true)
                .addResolver(resolver);
    }

    public static class ReactResourceResolver implements ResourceResolver {
        private static final String REACT_DIR = "/static/";
        private static final String REACT_STATIC_DIR = "static";

        private final Resource index = new ClassPathResource(REACT_DIR + "index.html");
        private final List<String> rootStaticFiles = Arrays.asList(
                "asset-manifest.json", "manifest.json", "service-worker.js", "robots.txt");

        @Override
        public Resource resolveResource(HttpServletRequest request,
                                        @NonNull String requestPath,
                                        @NonNull List<? extends Resource> locations,
                                        @NonNull ResourceResolverChain chain) {
            return resolve(requestPath, locations);
        }

        @Override
        public String resolveUrlPath(@NonNull String resourcePath,
                                     @NonNull List<? extends Resource> locations,
                                     @NonNull ResourceResolverChain chain) {
            Resource resolvedResource = resolve(resourcePath, locations);
            if (resolvedResource == null)
                return null;

            try {
                return resolvedResource.getURL().toString();
            } catch (Exception e) {
                return resolvedResource.getFilename();
            }
        }

        private Resource resolve(String requestPath, List<? extends Resource> locations) {
            if (requestPath == null) return null;

            if (rootStaticFiles.contains(requestPath) || requestPath.startsWith(REACT_STATIC_DIR)) {
                return new ClassPathResource(REACT_DIR + requestPath);
            } else {
                return index;
            }
        }
    }
}
