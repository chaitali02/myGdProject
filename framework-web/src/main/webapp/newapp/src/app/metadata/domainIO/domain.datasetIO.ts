import { Dataset } from '../domain/domain.dataset';
import { FilterInfoIO } from './domain.filterInfoIO';
import { AttributeSourceIO } from './domain.AttributeSourceIO';

export class DatasetIO{
    datasetData : Dataset;
    filterInfo : Array<FilterInfoIO>;
    attributeInfo : Array<AttributeSourceIO>;
}