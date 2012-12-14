package edu.upenn.mkse212.client;

import com.google.gwt.core.client.JavaScriptObject;


// NOTE: The 'onclick' function below calls a "void drawNodeAndNeighbors(final String s)" 
// method in edu.upenn.cis.mkse212.hw4.client.FriendViewer. If you use a different class
// name or an even slightly different method signature, you will get errors at runtime,
// even though the compilation will succeed.





public class FriendVisualization {
	
	PennBook parent;
	
	public FriendVisualization(PennBook theParent) {
		this.parent = theParent; 
	}

	public static final native void addToGraph(final JavaScriptObject ht, String json) /*-{
		var content = JSON.parse(json);
		ht.op.sum(content, { type: "fade:con", fps: 4, duration: 1000, hideLabels: true }); 
	}-*/;
	
	/**
	 * Creates a JavaScript Infovis Toolkit hypertree
	 * 
	 * @param json
	 * @return
	 */
	public final native JavaScriptObject createGraph(final String content, final ProfilePanel parent) /*-{
		 //init Hypertree
		var ht = new $wnd.$jit.Hypertree({
		    //id of the visualization container
		    injectInto: 'infovis',
		    //By setting overridable=true,
		    //Node and Edge global properties can be
		    //overriden for each node/edge.
		    Node: {
		        //overridable: true,
		        'transform': false,
		        color: "#f00"
		    },
		
		    Edge: {
		        //overridable: true,
		        color: "#088"
		    },
		    //calculate nodes offset
		    offset: 0.2,
		    //Change the animation transition type
		    transition: $wnd.$jit.Trans.Back.easeOut,
		    //animation duration (in milliseconds)
		    duration:1000,
		
		    //Attach event handlers on label creation.
		    onCreateLabel: function(domElement, node){
		        domElement.innerHTML = node.name;
		        domElement.style.cursor = "pointer";
		        domElement.onclick = function () {
					parent.@edu.upenn.mkse212.client.ProfilePanel::display(Ljava/lang/String;)(node.name);
					console.debug("Clicked");
		            ht.onClick(node.id, { hideLabels: false });
		
		        };
		    },
		    //This method is called when moving/placing a label.
		    //You can add some positioning offsets to the labels here.
		    onPlaceLabel: function(domElement, node){
		        var width = domElement.offsetWidth;
		        var intX = parseInt(domElement.style.left);
		        intX -= width / 2;
		        domElement.style.left = intX + 'px';
		    },
		
		    onAfterCompute: function(){
		    }
		});
		//load JSON graph.
                json = JSON.parse(content);
		ht.loadJSON(json, 1);
		//compute positions and plot
		ht.refresh();
		//end
		ht.controller.onBeforeCompute(ht.graph.getNode(ht.root));
		ht.controller.onAfterCompute();
		return ht;
	}-*/;



}
