import { Profile } from './../domain/domain.profile';
import { FilterInfoIO } from './domain.filterInfoIO';

export class ProfileIO {

	// profile: Profile;
	// dependsOn : MetaIdentifierHolder;
	// attributeInfo : AttributeRefHolder[];
	// filterInfo : Array<FilterInfo>;
	
	profile: Profile;
	filterInfoIo: Array<FilterInfoIO>;
	isFormulaExits: boolean;
	isFunctionExits: boolean;
	isAttributeExits: boolean;
	isSimpleExits: boolean;
	isParamlistExits: boolean;
	isDatasetExits: boolean;
}