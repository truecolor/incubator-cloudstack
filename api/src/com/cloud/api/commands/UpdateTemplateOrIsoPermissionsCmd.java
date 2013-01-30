// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.api.commands;

import java.util.List;

import org.apache.log4j.Logger;

import com.cloud.api.ApiConstants;
import com.cloud.api.BaseCmd;
import com.cloud.api.IdentityMapper;
import com.cloud.api.Parameter;
import com.cloud.api.ServerApiException;
import com.cloud.api.response.SuccessResponse;
import com.cloud.exception.InvalidParameterValueException;

public abstract class UpdateTemplateOrIsoPermissionsCmd extends BaseCmd {
    public Logger s_logger = getLogger();
    protected String s_name = getResponseName();

    // ///////////////////////////////////////////////////
    // ////////////// API parameters /////////////////////
    // ///////////////////////////////////////////////////

    @Parameter(name = ApiConstants.ACCOUNTS, type = CommandType.LIST, collectionType = CommandType.STRING, description = "a comma delimited list of accounts. If specified, \"op\" parameter has to be passed in.")
    private List<String> accountNames;

    @IdentityMapper(entityTableName="vm_template")
    @Parameter(name = ApiConstants.ID, type = CommandType.LONG, required = true, description = "the template ID")
    private Long id;

    @Parameter(name = ApiConstants.IS_FEATURED, type = CommandType.BOOLEAN, description = "true for featured template/iso, false otherwise")
    private Boolean featured;

    @Parameter(name = ApiConstants.IS_PUBLIC, type = CommandType.BOOLEAN, description = "true for public template/iso, false for private templates/isos")
    private Boolean isPublic;
    
    @Parameter(name = ApiConstants.IS_EXTRACTABLE, type = CommandType.BOOLEAN, description = "true if the template/iso is extractable, false other wise. Can be set only by root admin")
    private Boolean isExtractable;

    @Parameter(name = ApiConstants.OP, type = CommandType.STRING, description = "permission operator (add, remove, reset)")
    private String operation;
    
    @IdentityMapper(entityTableName="projects")
    @Parameter(name = ApiConstants.PROJECT_IDS, type = CommandType.LIST, collectionType = CommandType.LONG, description = "a comma delimited list of projects. If specified, \"op\" parameter has to be passed in.")
    private List<Long> projectIds;

    // ///////////////////////////////////////////////////
    // ///////////////// Accessors ///////////////////////
    // ///////////////////////////////////////////////////

    public List<String> getAccountNames() {
        if (accountNames != null && projectIds != null) {
            throw new InvalidParameterValueException("Accounts and projectIds can't be specified together");  
        }
        
        return accountNames; 
    }

    public Long getId() {
        return id;
    }

    public Boolean isFeatured() {
        return featured;
    }

    public Boolean isPublic() {
        return isPublic;
    }
    
    public Boolean isExtractable() {
        return isExtractable;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public List<Long> getProjectIds() {
        if (accountNames != null && projectIds != null) {
            throw new InvalidParameterValueException("Accounts and projectIds can't be specified together");  
        }
        return projectIds;
    }

    // ///////////////////////////////////////////////////
    // ///////////// API Implementation///////////////////
    // ///////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }

    protected String getResponseName() {
        return "updatetemplateorisopermissionsresponse";
    }

    protected Logger getLogger() {
        return Logger.getLogger(UpdateTemplateOrIsoPermissionsCmd.class.getName());
    }

    @Override
    public void execute(){
        boolean result = _templateService.updateTemplateOrIsoPermissions(this);
        if (result) {
            SuccessResponse response = new SuccessResponse(getCommandName());
            this.setResponseObject(response);
        } else {
            throw new ServerApiException(BaseCmd.INTERNAL_ERROR, "Failed to update template/iso permissions");
        }
    }
}