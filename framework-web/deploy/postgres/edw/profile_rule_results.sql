DROP TABLE profile_rule_results;
	   
	CREATE TABLE profile_rule_results
	(
	  datapoduuid text,
	  datapodversion text,
	  attributeid text,
	  minval double precision,
	  maxval double precision,
	  avgval double precision,
	  medianval double precision,
	  stddev double precision,
	  numdistinct integer,
	  perdistinct double precision,
	  numnull integer,
	  pernull double precision,
	  sixsigma double precision,
	  version integer
	);
