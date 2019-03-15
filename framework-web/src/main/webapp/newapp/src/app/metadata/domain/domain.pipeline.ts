import { Status } from './domain.status';
import { Stage } from '../enums/stage';


export class Pipeline{

    private xPos : Number;
    private yPos : Number;
    private stages : Stage;
    private statusList : Status;
}
    