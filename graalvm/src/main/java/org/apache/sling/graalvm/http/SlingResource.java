package org.apache.sling.graalvm.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.sling.graalvm.osgi.SlingContext;
import org.apache.sling.graalvm.sling.MockServletResolver;
import org.apache.sling.api.request.RequestProgressTracker;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.testing.sling.MockSlingHttpServletResponse;
import org.apache.sling.engine.SlingRequestProcessor;
import org.apache.sling.engine.impl.request.SlingRequestProgressTracker;

@Path("/sling/{resourcePath: [^/][a-zA-Z/_0-9\\.]*}")
@Produces(MediaType.APPLICATION_JSON)
public class SlingResource {

    @Context
    private HttpServletRequest request;

    static class HttpResponse extends MockSlingHttpServletResponse {
        @Override
        public void setContentLength(int length) {
        }
    }

     @GET
    public Response sling(@PathParam("resourcePath") String resourcePath) throws IOException {
        final SlingRequestProcessor p = SlingContext.get().getService(SlingRequestProcessor.class);
        assert (p != null);
        final ResourceResolver resolver = SlingContext.get().getService(ResourceResolver.class);
        assert (resolver != null);
        request.setAttribute(RequestProgressTracker.class.getName(), new SlingRequestProgressTracker(request));

        try {
            p.processRequest(request, new HttpResponse(), resolver);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final Resource r = (Resource)request.getAttribute(MockServletResolver.RESOURCE_ATTR);
        return Response.ok(r).build();
    }
}