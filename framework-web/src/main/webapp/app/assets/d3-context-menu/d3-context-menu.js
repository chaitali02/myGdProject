d3.contextMenu = function (menu, openCallback) {
	
	// create the div element that will hold the context menu
	d3.selectAll('.d3-context-menu').data([1])
		.enter()
		.append('div')
		.attr('class', 'd3-context-menu');

	// close menu
	d3.select('body').on('click.d3-context-menu', function() {
		d3.select('.d3-context-menu').style('display', 'none');
		d3.select('.d3-context-menu').selectAll("ul li").selectAll("ul").remove();
	});

	//this gets executed when a contextmenu event occurs
	return function(data, index) {	
		var elm = this;
		var y=d3.event.pageY;
		var x=d3.event.pageX+21;
		var height,
		width,
		margin = 0.1 //fraction of width
		style = {
			'text': {
				'font-size':'13px',
				'font-family':'Open Sans',
				'padding':'10px'
			}
		};

		d3.selectAll('.d3-context-menu').html('');
		
		var list = d3.selectAll('.d3-context-menu').append('ul').attr('class','parent-menu');
	       	list.selectAll('li').data(menu).enter()
			.append('li')
			.attr('class',function(d){
				if(d.chidernItems.length > 0){
					return 'menu-entry parent-menu-after';
				}else{
					return 'menu-entry'
				}
			})
			.on('click', function(d, i) {
				d.onMenuClick(d, data, index);
				if(d.chidernItems.length>0){ 
					d3.select('.d3-context-menu').selectAll("ul li").selectAll("ul").remove();
                    d3.select(this)
						.append("ul")
					    //.style("top",d3.event.pageY-y-10+ "px")
                    	.selectAll("li")
                        .data(d.chidernItems)
						.enter().append("li")
						.attr('class','child-menu-entry')
						//.text(function(d) { return d.title; })
						.style("color",'white')
                    	.on("mouseenter", function(d,i){
							d3.select(this).style("color","#fefefe");
						   //d.onMouseOver(elm,data,index);
                    	})
                		.on('click', function(d, i) {
							d3.event.stopPropagation();
							d.onChildMenuClick(d, data, index);
							d3.select('.d3-context-menu').selectAll("ul li").selectAll("ul").remove();
							d3.select('.d3-context-menu').style('display', 'none');			
                   		})
                    	.on('mouseout',function(d,i){
							d3.select(this).style("color","white")
                       		//d3.select(this).remove(); 
						});
					d3.selectAll('.child-menu-entry')
						.append('img')
						.attr('x', 10)
						.attr('y', 10)
						.attr('width','15')
						.attr('height','15')
						.attr('stroke-width','1')
						.attr('stroke','#ccc')
						.attr('xmlns:xlink',"http://www.w3.org/1999/xlink")
						.attr('src',function(d){return d.image;});
					d3.selectAll('.child-menu-entry')
			  			.append('span')
			  			.text(function(d){ return d.title; })
			  			.attr('x', x+25)
			  			.attr('y', function(d, i){ return y + (i * height); })
			  			.attr('dy', height - 10 - margin / 2)
			  			.attr('dx', margin)
						.style(style.text);
                }
				else{
					d3.select('.d3-context-menu').style('display', 'none');
					return false;
				}
			})

            .on('mouseenter',function(d,i){
				//d.onMouseOver(elm,data,index);
				//d3.select('.d3-context-menu').selectAll("ul li").selectAll("ul").remove();
			})
			
            .on('mouseleave',function(d,i){
            	if(d.chidernItems.length==0){
                   d3.select(this).selectAll("ul").remove(); 
                }
			});
			
			d3.selectAll('.menu-entry')
				.append('img')
            	.attr('x', 10)
            	.attr('y', 10)
              	.attr('width','15')
              	.attr('height','15')
             	.attr('stroke-width','1')
             	.attr('stroke','#ccc')
              	.attr('xmlns:xlink',"http://www.w3.org/1999/xlink")
              	.attr('src',function(d){return d.image;});

			d3.selectAll('.menu-entry')
			  	.append('span')
			  	.text(function(d){ return d.title; })
			  	.attr('x', x+25)
			  	.attr('y', function(d, i){ return y + (i * height); })
			  	.attr('dy', height - 10 - margin / 2)
			  	.attr('dx', margin)
				.style(style.text);
				  
		// the openCallback allows an action to fire before the menu is displayed
		// an example usage would be closing a tooltip
		if (openCallback) openCallback(data, index);
		// display context menu
	
		d3.select('.d3-context-menu')
			.style('left', (d3.event.pageX- 2) + 'px')
			.style('top', (d3.event.pageY) + 'px')
			.style('display', 'block');

		d3.event.preventDefault();
	};
};