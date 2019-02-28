import { IngestRule } from './../domain/domain.ingestRule';
import { FilterInfoIO } from './domain.filterInfoIO';

export class DataIngestRuleIO {
    ingestRule: IngestRule;
    filterInfo: Array<FilterInfoIO>;
    attributeMap: any;
}
