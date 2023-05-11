package com.lergo.framework.config;

import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.PathProvider;
import springfox.documentation.spring.web.paths.DefaultPathProvider;

import static springfox.documentation.spring.web.paths.Paths.ROOT;
import static springfox.documentation.spring.web.paths.Paths.removeAdjacentForwardSlashes;

public class IPathProvider implements PathProvider {

    private final SwaggerProperties pathProperties;

    public IPathProvider(SwaggerProperties pathProperties) {
        this.pathProperties = pathProperties;
    }

    /**
     * The base path to the swagger api documentation.
     * <p>
     * Typically, docs are served from &lt;yourApp&gt;/api-docs so a relative resourceListing path will omit the api-docs
     * segment. E.g. Relative: "path": "/" Absolute: "path": "<a href="http://localhost:8080/api-docs">...</a>"
     *
     * @return the documentation base path
     */
    protected String getDocumentationPath() {
        return ROOT;
    }

    /**
     * The relative path to the operation, from the basePath, which this operation describes. The value SHOULD be in a
     * relative (URL) path format.
     * <p>
     * Includes the apiResourcePrefix
     *
     * @param operationPath operation path
     * @return the relative path to the api operation
     */
    @Override
    public String getOperationPath(String operationPath) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath(pathProperties.getBasePath());
        return removeAdjacentForwardSlashes(uriComponentsBuilder.path(operationPath).build().toString());
    }

    /**
     * Corresponds to the path attribute of a swagger Resource Object (within a Resource  Listing).
     * <p>
     * This method builds a URL based off of
     *
     * @param groupName      the group name for this Resource Object e.g. 'default'
     * @param apiDeclaration the identifier for the api declaration e.g 'business-controller'
     * @return the resource listing path
     * @see DefaultPathProvider getDocumentationPath by appending the swagger group and apiDeclaration
     */
    @Override
    public String getResourceListingPath(String groupName, String apiDeclaration) {
        String candidate = agnosticUriComponentBuilder(getDocumentationPath()).pathSegment(groupName, apiDeclaration)
                .build().toString();
        return removeAdjacentForwardSlashes(candidate);
    }

    private UriComponentsBuilder agnosticUriComponentBuilder(String url) {
        UriComponentsBuilder uriComponentsBuilder;
        if (url.startsWith("http")) {
            uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        } else {
            uriComponentsBuilder = UriComponentsBuilder.fromPath(url);
        }
        return uriComponentsBuilder;
    }
}
