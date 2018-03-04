package com.inferyx.framework.domain;

public class ImportIdentifierHolder {
	private MetaIdentifier ref;
	private String status;
	
	public ImportIdentifierHolder() {
		super();
	}

	public ImportIdentifierHolder(MetaIdentifier ref) {
		super();
		this.ref = ref;
	}

	public ImportIdentifierHolder(MetaIdentifier ref, String status) {
		super();
		this.ref = ref;
		this.status = status;
	}

	public MetaIdentifier getRef() {
		return ref;
	}

	public void setRef(MetaIdentifier ref) {
		this.ref = ref;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ImportIdentifierHolder [ref=" + ref + ", status=" + status + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImportIdentifierHolder other = (ImportIdentifierHolder) obj;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}
}
