export class Key{

    private uuid;
	private version;
    private name;
	
	public Key(uuid?: String , version?: String ) {
		this.uuid = uuid;
		this.version = version;
}
}