package org.devgateway.ocds.web.rest.controller.flags.crosstab;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.persistence.mongo.flags.FlagsConstants;
import org.devgateway.ocds.web.rest.controller.flags.AbstractFlagCrosstabController;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by mpostelnicu on 28-Mar-17.
 */
@RestController
@CacheConfig(keyGenerator = "genericPagingRequestKeyGenerator", cacheNames = "genericPagingRequestJson")
@Cacheable
public class FlagI007CrosstabController extends AbstractFlagCrosstabController {

    @Override
    protected String getFlagProperty() {
        return FlagsConstants.I007_VALUE;
    }

    @Override
    @ApiOperation(value = "Crosstab for flag i007")
    @RequestMapping(value = "/api/flags/i007/crosstab",
            method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> flagStats(@ModelAttribute @Valid YearFilterPagingRequest filter) {
        return super.flagStats(filter);
    }
}
