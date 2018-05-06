/*
 * Copyright (C) 2016 - 2017 AptiTekk, LLC. (https://AptiTekk.com/) - All Rights Reserved
 * Unauthorized copying of any part of AptiLink, via any medium, is strictly prohibited.
 * Proprietary and confidential.
 */

package com.mitchtalmadge.cryptex.web.api;

import com.mitchtalmadge.cryptex.service.LogService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class APIControllerAbstract {

    @Autowired
    protected LogService logService;

    protected ModelMapper modelMapper = new ModelMapper();

    public APIControllerAbstract() {
        modelMapper.getConfiguration().setFieldMatchingEnabled(true);
    }

}
