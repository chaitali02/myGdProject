import { Operator } from './domain.operator';
import { Status } from './domain.status';

export class Task{

    
	private taskId : String;
	private dependsOn : String[];//= new ArrayList<>(0);
	private name : String;
	private operators : Operator[]; //= new ArrayList<Operator>();
	private xPos : Number ;
	private yPos : Number;
	private statusList : Status[];//= new ArrayList<>();;
	
}