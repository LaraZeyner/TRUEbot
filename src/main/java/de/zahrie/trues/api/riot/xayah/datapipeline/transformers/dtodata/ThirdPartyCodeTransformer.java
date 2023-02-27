package de.zahrie.trues.api.riot.xayah.datapipeline.transformers.dtodata;

import com.merakianalytics.datapipelines.PipelineContext;
import com.merakianalytics.datapipelines.transformers.AbstractDataTransformer;
import com.merakianalytics.datapipelines.transformers.Transform;
import de.zahrie.trues.api.riot.xayah.types.data.thirdpartycode.VerificationString;

public class ThirdPartyCodeTransformer extends AbstractDataTransformer {
    @Transform(from = de.zahrie.trues.api.riot.xayah.types.dto.thirdpartycode.VerificationString.class, to = VerificationString.class)
    public VerificationString transform(final de.zahrie.trues.api.riot.xayah.types.dto.thirdpartycode.VerificationString item, final PipelineContext context) {
        final VerificationString string = new VerificationString();
        string.setPlatform(item.getPlatform());
        string.setString(item.getString());
        string.setSummonerId(item.getSummonerId());
        return string;
    }

    @Transform(from = VerificationString.class, to = de.zahrie.trues.api.riot.xayah.types.dto.thirdpartycode.VerificationString.class)
    public de.zahrie.trues.api.riot.xayah.types.dto.thirdpartycode.VerificationString transform(final VerificationString item, final PipelineContext context) {
        final de.zahrie.trues.api.riot.xayah.types.dto.thirdpartycode.VerificationString string =
            new de.zahrie.trues.api.riot.xayah.types.dto.thirdpartycode.VerificationString();
        string.setPlatform(item.getPlatform());
        string.setString(item.getString());
        string.setSummonerId(item.getSummonerId());
        return string;
    }
}
