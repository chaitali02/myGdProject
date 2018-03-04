/**
 * graph
 */
MetadataModule=angular.module('MetadataModule');
MetadataModule.directive('metadataDatapodDirective', function ($compile,$rootScope,dagMetaDataService) {
    return {
    	scope : {
    		  graphdata: "=",
    		  selectChild:"=",
    		  checkdata:"=",
    		  getnodedata:'&',
    		  getnoderouting:'&'
    	},

      link: function ($scope, element, attrs) {
        $scope.$watch('graphdata', function(newValue, oldValue){
        	if($scope.graphdata == null){
        		return false;
        	}
          element.css('width','100%');
          var menus = ["Show Details"];
          var objects=[
        	  {"key":"application","value":"metaListapplication"},
        	  {"key":"user","value":"adminListuser"},
        	  {"key":"datasource","value":"metaListdatasource"},
        	  ];
          width = 900,
          height = 500;
          color = d3.scale.category20();
          radius = d3.scale.sqrt()
            .range([0, 6]);
          d3.select("svg").remove();
          d3.select(".tooltipFocus").remove();
          svg = d3.select("div.show-graph-body").append("svg")
            .attr("class","graph-svg")
            //.attr("height", height)
          .on('click',function(e){
            d3.selectAll(".vz-weighted_tree-tip").remove()
          });
          var div = d3.select("div.portlet-body").append("div")
            .attr("class", "tooltipFocus")
            .style("opacity", 0);
          var force = d3.layout.force()
            .size([width, height])
            .charge(-400)
            .linkDistance(function(d) { return radius(d.src.size) + radius(d.dst.size) + 20; });
          var graph=$scope.graphdata;
          var edges=[];
          graph.links.forEach(function(e) {
              // Get the source and target nodes
          var sourceNode = graph.nodes.filter(function(n) { return n.id === e.src; })[0],
          targetNode = graph.nodes.filter(function(n) { return n.id === e.dst; })[0];
          // Add the edge to the array
          //console.log(sourceNode);
          edges.push({source: sourceNode, target: targetNode, relationType : e.relationType});
          });

          force
            .nodes(graph.nodes)
            .links(edges)
            .linkDistance(100) //jitender200
            .on("tick", tick)
            .start();

            link = svg.selectAll(".link")
              .data(edges)
              .enter().append("g")
              .attr("class", "link")
            linkText = svg.selectAll(".link")
              .append("text")
              .attr("class", "link-label")
              .attr("font-family", "Arial, Helvetica, sans-serif")
              .attr("fill", "Black")
              .style("font", "normal 12px Arial")
              .attr("dy", ".35em")
              .attr("text-anchor", "middle")
              .text(function(d) {
                return d.relationType;
              });
            link.append("line")
              .style("stroke-width", function(d) { return (d.bond * 2 - 1) * 2 + "px"; })
              node = svg.selectAll(".node5837fd0e27505225aa48bc3b")
              .data(graph.nodes)
              .enter().append("g")
              .attr("class", "node")
              .call(force.drag);
              node.append("circle")
              .attr("r", function(d) { return d.nodeType == "datapod1" ? radius(15): radius(8); })
              .style("stroke-width",1)    // set the stroke width //jitender
              .style("stroke", "73879C") //jitender 73879C
              .style('fill',function(d){
            	 return getColorCode(d) //function call for color code;
              })
              .on('contextmenu', function(d,i) {

            	  var Nodedata=d
            	  d3.selectAll('.context-menu').data([1])
                  	.enter()
                  	.append('div')
                    .attr('class', 'context-menu');
                // close menu
            	  d3.select('body').on('click.context-menu', function() {

            	  d3.select('.context-menu').style('display', 'none');
                });
                // this gets executed when a contextmenu event occurs
                d3.selectAll('.context-menu')
                  .html('')
                  .append('ul')
                  .selectAll('li')
                  .data(menus).enter()
                  .append('li')

                .on('click' , function(d) {
                	//alert(d)
                	//alert(JSON.stringify(Nodedata))
                	createRouting(Nodedata,d);
                	d3.select('.context-menu').style('display', 'none');
                	//console.log(d); return d;

                })

                   .text(function(d) { return d; });
                   d3.select('.context-menu').style('display', 'none');
                  // show the context menu
                  d3.select('.context-menu')
                    .style('left', (d3.event.pageX - 2) + 'px')
                    .style('top', (d3.event.pageY - 2) + 'px')
                    .style('display', 'block');
                 d3.event.preventDefault();
            	     //alert("dfdf")

              })
              .on("dblclick",function(d){
                div.transition()
                .duration(500)
                .style("opacity", 0);
                createDataTip(d3.event.pageX,d3.event.pageY, d.nodeType, d.name,d.id,d.version,d.desc,d.createdOn,d.createdBy,d.active);
              })

              .on("click",function(d){
                div.transition()
                .duration(500)
                .style("opacity", 0);
              })
              .on("mouseover", function(d) {
            	  var color;
            	  if($('.portlet-body').width()<= 726){
            		  div.transition()
                     .duration(200)
                     .style("opacity", .9);
                    div .html("")
                      .style("left", (d3.event.pageX - 90) + "px")
                      .style("top", (d3.event.pageY - 128) + "px");
            	  }
                else{
            	    div.transition()
                     .duration(200)
                     .style("opacity", .9);
                   // console.log(JSON.stringify(d))
                  div.html("")
                   /*.style("left", (d3.event.pageX - 290) + "px")
                .style("top", (d3.event.pageY - 128) + "px");  */
                      .style("left", (d.px+50)+ "px")
                      .style("top", (d.py+90)+ "px");
                }

            	//  console.log(JSON.stringify(d))
            	  d3.select("div.portlet-body").select(".tooltipFocus").append("div").attr("class","tooltipcustom");
            	  d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").append("span").attr("class","one").style("float","left").style("background-color",getColorCode(d));
                d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").append("div").attr("class","header1").html("&nbsp&nbsp"+d.nodeType).style("left", (d3.event.pageX) + "px").style("top", (d3.event.pageY - 28) + "px");
                d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").append("div").attr("class","header4").html("").style("left", (d3.event.pageX) + "px").style("top", (d3.event.pageY - 28) + "px");

                d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").select(".header4").append("label").attr("class","header2label").html("Id");
                d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").select(".header4").append("span").attr("class","header2span").html(d.id);

                d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").append("div").attr("class","header2").html("").style("left", (d3.event.pageX) + "px").style("top", (d3.event.pageY - 28) + "px");
                d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").select(".header2").append("label").attr("class","header2label").html("Name");
                d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").select(".header2").append("span").attr("class","header2span").html(d.name);
                d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").append("div").attr("class","header3").html("").style("left", (d3.event.pageX) + "px").style("top", (d3.event.pageY - 28) + "px");
                d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").select(".header3").append("label").attr("class","header2label").html("Version");
                d3.select("div.portlet-body").select(".tooltipFocus").select(".tooltipcustom").select(".header3").append("span").attr("class","header2span").html(d.version);
              })

             .on("mouseout", function(d) {
                 div.transition()
                    .duration(500)
                    .style("opacity", 0);
              });
              // .style("fill", function(d) { return color(d.title); });
               node.append("text")
                   .attr("dy", ".35em")
                   .attr("text-anchor", "middle")
                   .attr('class','abe')
                   //.style('fill',function(d){return (d.nodeType == 'datapod' ? 'white':'#4a6c8c')}) jitender
                   .text(function(d) { return d.name })
                   .style('font-size',function(d){return (d.nodeType == 'datapod1' ? 'larger':'')});

              function tick() {
                  link.selectAll("line")
                      .attr("x1", function(d) { return d.source.x; })
                      .attr("y1", function(d) { return d.source.y; })
                      .attr("x2", function(d) { return d.target.x; })
                      .attr("y2", function(d) { return d.target.y; });

                  linkText.attr("x", function(d) {
                    return ((d.source.x + d.target.x)/2);
                  })
                  .attr("y", function(d) {
                    return ((d.source.y + d.target.y)/2);
                  });
                  node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
                }

              function getColorCode(d){
            	    var color;
            	    if(d.nodeType=="application")
              		  color="#f794e0";
              	 	  else
              	 	 if(d.nodeType=="appInfo")
                 		  color="#f2bae5";
              	 	 else if(d.nodeType=="dataset")
             		    color="#0d01b5";

              	 	else if(d.nodeType=="Value")
                		  color="#dcfcdb";
              	 	else if(d.nodeType=="loadexec")
              		  color="#e6ed63";

              	 	else if(d.nodeType=="function")
              		  color="#f79742";
              	    else if(d.nodeType=="load")
            		  color="#933f5b";

              	    else if(d.nodeType=="map")
         	    	  color="#f75b8f";
         	        else if(d.nodeType=="attributeMap")
       	    	    color="#f49fbb";

            	    else if(d.nodeType=="condition")
            		    color="#48C9B0";
            	    else if(d.nodeType=="ConditionValue")
            		    color="#E8F8F5";
          	      else if(d.nodeType=="Conditioninfo")
          	    	  color="#A3E4D7";
          	      else if(d.nodeType=="dag")
          	    	  color="#2f89ef";
          	      else if(d.nodeType=="stages")
          	    	  color="#7bb3f2";
                  else if(d.nodeType=="tasks")
                	  color="#adcef4";
                  else if(d.nodeType=="operators")
                	  color="#d9e7f7";
                  else if(d.nodeType=="rulegroup")
                	  color="#ffb3b3";

          	      else if(d.nodeType=="DagExec")
          	    	  color="#e59866";
          	      else if(d.nodeType=="Stage")
          	    	  color="#F5CBA7";
          	      else if(d.nodeType=="Task")
          	    	  color="#FDEBD0";
          	      else if(d.nodeType=="dashboard")
          	    	  color="#AFFF75";
          	      else if(d.nodeType=="sectioninfo")
          	    	  color="#c4ff9a";
      	          else if(d.nodeType=="MetaId")
      	        	  color="#D7FFBA";
      	        else  if(d.nodeType=="datapod")
      	    	  color="#606df2";
      	        else if(d.nodeType=="attributes")
          	      color="#b3b8f9";
                else if(d.nodeType=="Attribute")
            	  color="#dbddf9";
          	      else if(d.nodeType=="Dataset")
          	    	  color="#e5ff7e";
                  else if(d.nodeType=="DatasetFilterInfo")
                	  color="#fbffab";
                  else if(d.nodeType=="datasource")
                	  color="#EC7063";
          	      else if(d.nodeType=="datastore")
          	    	  color="#efefef";
          	      else if(d.nodeType=="Dimension")
          	    	  color="#85ff7e";
          	      else if(d.nodeType=="expression")
          	    	  color="#740491";
          	      else if(d.nodeType=="expressionInfo")
          	    	  color="#946C9E";
          	      else if(d.nodeType=="operand")
              	  color="#EBE896";
          	      else if(d.nodeType=="filter")
          	    	  color="#5BF5ED";
          	      else if(d.nodeType=="filterInfo")
          	    	  color="#CFFAF8";
                  else if(d.nodeType=="formula")
          	    	  color="#36b731";
          	      else if(d.nodeType=="formulaInfo")
            	    color="#b4f7b2";
          	      else if(d.nodeType=="Group")
          		      color="#fce5cd";
          	      else if(d.nodeType=="Load")
          		      color="#d5a6bd";
                  else if(d.nodeType=="loadsource")
            	      color="#d5c2d2";
                  else if(d.nodeType=="loadtarget")
            	      color="#f0daec";
          	      else if(d.nodeType=="Map")
          		      color="#a2f967";
          	      else if(d.nodeType=="Mapping")
          		      color="#d7fcc0";
                  else if(d.nodeType=="Measure")
            	      color="#ffe599";
                  else if(d.nodeType=="Meta")
            	     color="#fcff85";
     	          else if(d.nodeType=="Privilege")
     	    	        color="#cfe2f3";
                  else if(d.nodeType=="relation")
            	      color="#75E108";
                  else if(d.nodeType=="Join")
            	      color="#e5ff7e";
                  else if(d.nodeType=="relationInfo")
              	    color="#9FDE60";
                  else if(d.nodeType=="joinKey")
              	    color="#C0EE93";
                  else if(d.nodeType=="RelationValue")
            	      color="#fffdd0";
                  else if(d.nodeType=="Role")
            	      color="#ebdef0";
                  else if(d.nodeType=="rule")
                  	   color="#f9877c";

                  else if(d.nodeType=="attributeInfo")
                    	  color="#f7bab4";
                  else if(d.nodeType=="Session")
            	      color="#d7fcc0";
                  else if(d.nodeType=="user")
            	      color="#b7b7b7";
                  else if(d.nodeType=="userGroup")
            	      color="#d7fcc0";
                  else if(d.nodeType=="VizpodExec")
            	      color="#fff8dc";
                  else if(d.nodeType=="vizpod")
            	      color="#41E0F5";
                  else if(d.nodeType=="vizpodKeys")
            	      color="#FFDAB9";
                  else if(d.nodeType=="vizpodGroups")
            	      color="#f4cccc";
                  else if(d.nodeType=="vizpodValues")
            	      color="#fce5cd";
                  else if(d.nodeType=="vizpodFilterInfo")
            	      color="#fff2cc";
                  else if(d.nodeType=="vizpoddimensions")
            	      color="#fff8dc";
		              else if(d.nodeType=="DQ")
		    	         color="#42FFB5";
                  else if(d.nodeType=="DQExec")
            	      color="#9dcbff";
                  else if(d.nodeType=="DQGroup")
            	      color="#bdeac0";
                  else if(d.nodeType=="DQInfo")
            	      color="#cceacf";
                  else if(d.nodeType=="DQGroupExec")
            	      color="#dceaab";
                  else if(d.nodeType=="dataqual")
            	      color="#05fcd7";
                  else if(d.nodeType=="profile")
            	      color="#fcca05";
                  else if(d.nodeType=="ruleExec")
            	      color="#e4ff9d";


                  else if(d.nodeType=="RuleSourceAttributes")
           	        color="#CBF6AA";
                  else if(d.nodeType=="activity")
            		    color="#fffdd8";
                  else if(d.nodeType=="ExpressionInfo1")
        	          color="#ffd9b5";
                  else if(d.nodeType=="FilterInfo")
        	          color="#fff0e0";
                  else if(d.nodeType=="Function")
           	        color="#ac9b6c";
                  else if(d.nodeType=="FunctionInfo")
            	      color="#d7c288";
                  else if(d.nodeType=="FunctionCategory")
                	  color="#e3d4ab";
                  else if(d.nodeType=="HIVE")
                    color="#efe6cf";
                  else if(d.nodeType=="IMPALA")
                    color="#efe6cf";
                  else if(d.nodeType=="ORACLE")
                    color="#efe6cf";
                  else
                    color="#ffffff"
                  return color;
                }

              function findElement(arr, propName, propValue) {
            	  for (var i=0; i < arr.length; i++){
            	    if (arr[i][propName] == propValue){
            	    	result= arr[i];
            	    	break;
            	    }
            	    else{
            	    	result= -1;
            	    	}

            	  }
            	  return result;
              }
              function createRouting(data,d){
            	  if(d =="Show Details"){
            		  console.log("method"+JSON.stringify(data));
                  console.log(data);
            		  // var x = findElement(objects,"key",data.nodeType);
            		  // if(x !=-1){
            			//   var nodedata={}
            			//   nodedata.uuid=data.id;
            			//   nodedata.version=data.version;
            			//   nodedata.type=x.key;
            			//   nodedata.route=x.value;
            			//   $scope.getnoderouting({selectChild:nodedata})
            		  // }

                  // var temp = data.uuid.split('_');
                  // if(!data.metaRef){
                  //   data.metaRef  = {
                  //     uuid: temp[0],
                  //     version:temp[1],
                  //     type: data.type
                  //   }
                  // }
                  dagMetaDataService.navigateTo(data.metaRef);

            	  }


              }
              function createDataTip(x,y,nodetype,h2,uuid,version,h5,h6,h7,h8) {
            	  var data ={};
            	  data.uuid=uuid;
            	  data.version=version;
            	  data.nodetype=nodetype;
            	  $scope.getnodedata({selectChild:data});

            	 /* MetadataSerivce.getGraphData(uuid)
                  .then(function (result) {
                	  $scope.$watch('graphdata', function(newValue, oldValue){
                    	  $scope.graphdata=result.data
            			   });


                      console.log(JSON.stringify(result.data));
                  });*/

                /*html = datatip.replace("HEADER1", h1);
                html = html.replace("HEADER2", h2);
                html = html.replace("HEADER3", h3);
                html = html.replace("HEADER4", h4);
                html = html.replace("HEADER5", h5);
                html = html.replace("HEADER6", h6);
                html = html.replace("HEADER7", h7);
                html = html.replace("HEADER8", h8);
                setTimeout(function(){
                  d3.select("body")
                  .append("div")
                  .attr("class", "vz-weighted_tree-tip")
                  .style("position", "absolute")
                  .style("left", (x - 290) + "px")
                  .style("top", (y - 128) + "px")
                  .style("opacity",0)
                  .html(html)
                  .transition().style("opacity",1);
                },100);*/
              }

        	  },true);
          }






      };
  });

MetadataModule.directive('datatableDirectivemeta', function ($compile,$rootScope) {
    return {
    	  scope : {
    		  name: "=",
    		  selectmeta:"=",

    		},
        link: function ($scope, element, attrs) {
            var values=$scope.selectmeta
        	  var href =$compile('<a ui-sref={'+values+'}></a>')($scope)[0].href;
            $scope.$watch('selectmeta', function(newValue, oldValue) {
          	  values=$scope.selectmeta;
            })
        	  $scope.$watch('name', function(newValue, oldValue){
                	  element.dataTable({
                		"bDestroy": true,
                		"aaData":$scope.name,
                		 "bAutoWidth": false,
                		 "aaSorting": [ [2,'desc']],
                		"columnDefs":[
                        { "width": "250px", "targets": 1 },
                        { "width": "60px", "targets": 2 },
                        { "width": "20%", "targets": 3 },
                        { "width": "13%", "targets": 4 },
                		{"width": "50px","targets":[6],"render": function (data,row,type ) {
              			       var href1=href.split("?")[0]+"/"+values+"?id="+type[1];
              			       var appenddiv;
              			       if(values == "dag" || values == "map" ){
              			    	   appenddiv = '<div class="btn-group pull-left btn-margin">'
     				                + '<button class="btn green btn-xs btn-outline dropdown-toggle" data-toggle="dropdown" aria-expanded="false">Action'
      			                 	+ '<i class="fa fa-angle-down"></i></button>'
      				                + '<ul class="dropdown-menu pull-right">'
      			                 	+ '<li><a href='+href1 +'&mode=true><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>'
      				                + '<li><a href='+href1 +'&mode=false><i class="fa fa-pencil-square-o" aria-hidden="true"></i> Edit </a></li>'
      		                		+ '<li><a href="javascript:;"><i class="fa fa-times" aria-hidden="true"></i>  Delete</a></li>'
      		                	    + '<li><a href="javascript:;"id='+type+' onclick=\"angular.element(this).scope().excutionDag(id)\"><i class="fa fa-tasks" aria-hidden="true"></i>  Execute</a></li>'
      		                		+ '<li><a href="javascript:;"id='+type+' onclick=\"angular.element(this).scope().createCopy(id)\"><i class="fa fa-clone" aria-hidden="true"></i>  Clone</a></li>'
      		                		+ '<li><a href="javascript:;"id='+type+' onclick=\"angular.element(this).scope().getDetail(id)\"><i class="fa fa-file-pdf-o" aria-hidden="true"></i>  Export</a></li>'
      		                		+ ' </ul></div>';
              			       }
              			       else if(values == "datapod"){

                			    	 appenddiv = '<div class="btn-group pull-left btn-margin">'
        				                + '<button class="btn green btn-xs btn-outline dropdown-toggle" data-toggle="dropdown" aria-expanded="false">Action'
         			                 	+ '<i class="fa fa-angle-down"></i></button>'
         				                + '<ul class="dropdown-menu pull-right">'
         			                 	+ '<li><a href='+href1 +'&mode=true><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>'
         				                + '<li><a href='+href1 +'&mode=false><i class="fa fa-pencil-square-o" aria-hidden="true"></i> Edit </a></li>'
         		                		+ '<li><a href="javascript:;"><i class="fa fa-times" aria-hidden="true"></i>  Delete</a></li>'
         		                		+ '<li><a href="javascript:;"id='+type+' onclick=\"angular.element(this).scope().createCopy(id)\"><i class="fa fa-clone" aria-hidden="true"></i>  Clone</a></li>'
         		                		+ '<li><a href="javascript:;"id='+type+' onclick=\"angular.element(this).scope().getDetail(id)\"><i class="fa fa-file-pdf-o" aria-hidden="true"></i>  Export</a></li>'
         		                		+ '<li><a href="javascript:;"id='+type+' onclick=\"angular.element(this).scope().getDetailForUpload(id)\"><i class="fa fa-upload" aria-hidden="true"></i>  Upload</a></li>'
         		                		+ ' </ul></div>';

              			       }
              			       else{

              			    	 appenddiv = '<div class="btn-group pull-left btn-margin">'
      				                + '<button class="btn green btn-xs btn-outline dropdown-toggle" data-toggle="dropdown" aria-expanded="false">Action'
       			                 	+ '<i class="fa fa-angle-down"></i></button>'
       				                + '<ul class="dropdown-menu pull-right">'
       			                 	+ '<li><a href='+href1 +'&mode=true><i class="fa fa-eye" aria-hidden="true"></i> View </a></li>'
       				                + '<li><a href='+href1 +'&mode=false><i class="fa fa-pencil-square-o" aria-hidden="true"></i> Edit </a></li>'
       		                		+ '<li><a href="javascript:;"><i class="fa fa-times" aria-hidden="true"></i>  Delete</a></li>'
       		                		+ '<li><a href="javascript:;"id='+type+' onclick=\"angular.element(this).scope().createCopy(id)\"><i class="fa fa-clone" aria-hidden="true"></i>  Clone</a></li>'
       		                		+ '<li><a href="javascript:;"id='+type+' onclick=\"angular.element(this).scope().getDetail(id)\"><i class="fa fa-file-pdf-o" aria-hidden="true"></i>  Export</a></li>'
       		                		+ ' </ul></div>';
              			       }
              		           return  appenddiv
                    }}],
  	        		    'aoColumns':[{'sTitle':'datapodId',"bVisible":false},{'sTitle': 'UUID'},{'sTitle': 'Version'},{'sTitle': 'Name'},{'sTitle': 'Created By'},{'sTitle': 'Created On'},{'sTitle': 'Action'}]
  	        	    });
          });
      }
  };
});


MetadataModule.directive("formOnChange", function($parse, $interpolate){
	  return {
	    require: "form",
	    link: function(scope, element, attrs, form){
	      var cb = $parse(attrs.formOnChange);
	      element.on("change", function(){
	        cb(scope);
	      });
	    }
	  };
	});
