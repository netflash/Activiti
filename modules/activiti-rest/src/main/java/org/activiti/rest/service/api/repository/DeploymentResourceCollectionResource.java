/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.activiti.rest.service.api.repository;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.rest.common.application.ContentTypeResolver;
import org.activiti.rest.service.api.RestResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Frederik Heremans
 */
@RestController
public class DeploymentResourceCollectionResource {
  
  @Autowired
  protected RestResponseFactory restResponseFactory;
  
  @Autowired
  protected ContentTypeResolver contentTypeResolver;
  
  @Autowired
  protected RepositoryService repositoryService;

  @RequestMapping(value="/repository/deployments/{deploymentId}/resources", method = RequestMethod.GET, produces = "application/json")
  public List<DeploymentResourceResponse> getDeploymentResources(@PathVariable String deploymentId, HttpServletRequest request) {
    // Check if deployment exists
    Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
    if (deployment == null) {
      throw new ActivitiObjectNotFoundException("Could not find a deployment with id '" + deploymentId + "'.", Deployment.class);
    }
    
    List<String> resourceList = repositoryService.getDeploymentResourceNames(deploymentId);
    
    String serverRootUrl = request.getRequestURL().toString();
    serverRootUrl = serverRootUrl.substring(0, serverRootUrl.indexOf("/repository/deployments/"));
    
    // Add additional metadata to the artifact-strings before returning
    List<DeploymentResourceResponse> responseList = new ArrayList<DeploymentResourceResponse>();
    for (String resourceId : resourceList) {
      responseList.add(restResponseFactory.createDeploymentResourceResponse(deploymentId, resourceId, 
          contentTypeResolver.resolveContentType(resourceId), serverRootUrl));
    }
    return responseList;
  }
}
