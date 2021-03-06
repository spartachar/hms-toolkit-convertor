/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package com.huawei.hms.convertor.core.event.handler.project;

import com.huawei.hms.convertor.core.event.handler.AbstractCallbackHandler;
import com.huawei.hms.convertor.core.project.convert.CodeConvertService;
import com.huawei.hms.convertor.core.result.conversion.ConversionCacheManager;
import com.huawei.hms.convertor.core.result.conversion.ConversionItem;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.ServiceLoader;

/**
 * Revert event handler
 *
 * @since 2020-03-26
 */
@Slf4j
public class RevertEventHandler extends AbstractCallbackHandler<String, List<ConversionItem>>
    implements GeneralEventHandler {
    private CodeConvertService codeConvertService;

    private String projectPath;

    public RevertEventHandler(String projectPath) {
        ServiceLoader<CodeConvertService> codeConvertServices =
            ServiceLoader.load(CodeConvertService.class, getClass().getClassLoader());
        codeConvertService = codeConvertServices.iterator().next();
        this.projectPath = projectPath;
        codeConvertService.init(projectPath);
    }

    @Override
    public <T> void handleEvent(String projectPath, T data) {
        if (!(data instanceof String)) {
            log.error("Illegal event data type, expected: String");
            return;
        }
        String conversionId = (String) data;
        ConversionCacheManager.getInstance().correctCache(projectPath, conversionId, false);

        ConversionItem conversionItem = ConversionCacheManager.getInstance().getConversion(projectPath, conversionId);
        codeConvertService.revert(projectPath, conversionItem);
    }

    @Override
    public List<ConversionItem> getCallbackMessage() {
        return ConversionCacheManager.getInstance().getCorrectedItems(projectPath);
    }
}
