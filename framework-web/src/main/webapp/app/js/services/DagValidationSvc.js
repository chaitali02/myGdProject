InferyxApp=angular.module('InferyxApp');
InferyxApp.factory('dagValidationSvc',function(dagMetaDataService){
  var obj = {};
  function dagToStage (s,t,inboundLinks){
    return inboundLinks.length == 0 && s==='dag' && t ==='stage';
  }
  function stageToStage (s,t,inboundLinks){
    return inboundLinks.length == 0 && s==='stage' && t==='stage';
  }
  function stageToTask (s,t,inboundLinks){
    return inboundLinks.length == 0 && s==='stage' && dagMetaDataService.validTaskTypes.indexOf(t) > -1;
  }
  function taskToTask (s,t,inboundLinks,g,cellViewS,cellViewT){
    var notLinkedWithStage = true;
    if(inboundLinks.length > 0){
      var firstLinkedCell = g.getCell(inboundLinks[0].attributes.source.id);
      notLinkedWithStage = firstLinkedCell.attributes.elementType == 'stage' ? false : true;
    }
    if(cellViewS.model.attributes.parentStage && cellViewT.model.attributes.parentStage && cellViewS.model.attributes.parentStage != cellViewT.model.attributes.parentStage){
      return false;
    }
    return notLinkedWithStage && dagMetaDataService.validTaskTypes.indexOf(s) > -1 && dagMetaDataService.validTaskTypes.indexOf(t) > -1;
  }
  obj.validate = function (graph,cellViewS, magnetS, cellViewT, magnetT, end, linkView) {
    var isValid = false;
    // console.log(arguments);
    if(cellViewS.model.id === cellViewT.model.id){
      return false;
    }
    try{
      var cellT = graph.getCell(cellViewT.model.id)
      inboundLinks = graph.getConnectedLinks(cellT,{inbound : true});
      //console.log(inboundLinks);
    }
    catch(e){
      var inboundLinks = [];
    }

    var sType = cellViewS.model.attributes.elementType;
    var tType = cellViewT.model.attributes.elementType;
    isValid = dagToStage(sType,tType,inboundLinks) || stageToStage(sType,tType,inboundLinks) || stageToTask(sType,tType,inboundLinks) || taskToTask(sType,tType,inboundLinks,graph,cellViewS,cellViewT);
    return isValid;
  };
  return obj;
});
