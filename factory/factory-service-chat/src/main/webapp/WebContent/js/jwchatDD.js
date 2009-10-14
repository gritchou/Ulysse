var droptarget;
var ddbugitem;

Ext.onReady(function(){

	var ddissue = Ext.get('issue');
	ddissue.dd = new Ext.dd.DragSource('issue');	
	
	var ddWelcome = Ext.get('Welcome');
	ddWelcome.dd = new Ext.dd.DropTarget('Welcome');

	droptarget = ddWelcome.dd;
	ddbugitem = 'bugPanel';
	
	
	Ext.override(Ext.dd.DragSource, {
		afterDragDrop: function(droptarget, e, ddbugitem)
		{
			alert("Target = " + droptarget.getEl().id);
		}
	});
});

