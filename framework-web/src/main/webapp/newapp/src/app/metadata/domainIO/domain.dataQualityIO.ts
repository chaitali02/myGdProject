import { DataQuality } from './../domain/domain.dataQuality';
import { FilterInfoIO } from './domain.filterInfoIO';

export class DataQualityIO {

	dataQuality: DataQuality;
	filterInfoIo: Array<FilterInfoIO>;
	isFormulaExits: boolean;
	isFunctionExits: boolean;
	isAttributeExits: boolean;
	isSimpleExits: boolean;
	isParamlistExits: boolean;
	isDatasetExits: boolean;
}