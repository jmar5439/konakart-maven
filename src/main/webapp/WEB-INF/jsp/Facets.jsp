<%--
//
// (c) 2012 DS Data Systems UK Ltd, All rights reserved.
//
// DS Data Systems and KonaKart and their respective logos, are 
// trademarks of DS Data Systems UK Ltd. All rights reserved.
//
// The information in this document is free software; you can redistribute 
// it and/or modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
// 
// This software is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
--%>
<%@include file="Taglibs.jsp" %>
<%@page import="java.math.BigDecimal"%>
<% com.konakart.al.KKAppEng kkEng = (com.konakart.al.KKAppEng) session.getAttribute("konakartKey");  %>
<% com.konakart.al.ProductMgr prodMgr = kkEng.getProductMgr();%>
<% com.konakart.al.CategoryMgr catMgr = kkEng.getCategoryMgr();%>
<% boolean useSolr = kkEng.isUseSolr();%>

 
	<%if (prodMgr.getMinPrice() != null && prodMgr.getMaxPrice() != null && prodMgr.getMinPrice().compareTo(prodMgr.getMaxPrice()) != 0){%>
		<%BigDecimal minPrice = prodMgr.getMinPrice();%>
		<%BigDecimal maxPrice = prodMgr.getMaxPrice();%>
		<%BigDecimal minFilterPrice = (prodMgr.getProdSearch().getPriceFrom()==null)?minPrice:prodMgr.getProdSearch().getPriceFrom().setScale(0,BigDecimal.ROUND_HALF_DOWN);%>
		<%BigDecimal maxFilterPrice = (prodMgr.getProdSearch().getPriceTo()==null)?maxPrice:prodMgr.getProdSearch().getPriceTo().setScale(0,BigDecimal.ROUND_HALF_UP);%>
		<%String symbol = kkEng.getUserCurrency().getSymbolLeft();%>
		<script>
		    $(function() {
		        $( "#price-range-slider" ).slider({
		            range: true,
		            min: <%=minPrice%>,
		            max: <%=maxPrice%>,
		            values: [ <%=minFilterPrice%>, <%=maxFilterPrice%> ],
		            slide: function( event, ui ) {
		                $( "#amount" ).html( "<%=symbol%>"+ui.values[ 0 ] + " - " + "<%=symbol%>"+ui.values[ 1 ] );
		            },
			        stop: function( event, ui ) {
						document.getElementById('priceFromStr').value = ui.values[ 0 ];
						document.getElementById('priceToStr').value = ui.values[ 1 ];
						document.getElementById('priceFilterForm').submit();
			        }
		        });
		        $( "#amount" ).html("<%=symbol%>"+$( "#price-range-slider" ).slider( "values", 0 ) +
		            " - " +"<%=symbol%>"+ $( "#price-range-slider" ).slider( "values", 1 ) );
		    });
		</script> 
	<% } %>
	<div id="side-menu">	
  			<% if (prodMgr.getCurrentCategoriesLength() > 0){%>				
				<div class="side-menu-section">
					<h1><kk:msg  key="facet.tile.categories"/></h1>
					<ul>				
					<% for (int i = 0; i < prodMgr.getCurrentCategories().length; i++){ %>
						<% com.konakart.appif.CategoryIf cat = prodMgr.getCurrentCategories()[i];%>
						<%String name = (cat.getNumberOfProducts() < 0)? cat.getName(): cat.getName()+" ("+cat.getNumberOfProducts()+")"; %>
						<li>
						<%for (int j = 0; j < cat.getLevel(); j++){%>
							<%="&nbsp;"%>
						<% }%>
						<%String action;%>
						<%if (prodMgr.getNumSelectedFilters() > 0 || prodMgr.isPriceFilter()){%>
							<%action= "FilterSearchByCategory.action?catId=";%>
						<% } else { %>
							<%action= "SelectCat.action?catId=";%>
						<% }%>
						
						<%if ( cat.isSelected()) { %>
							<a href="<%=action%><%=cat.getId()%>&t=<%=prodMgr.getProdTimestamp()%>"><span class="current-cat"><%=name%></span></a>
						<% } else { %>
							<a href="<%=action%><%=cat.getId()%>&t=<%=prodMgr.getProdTimestamp()%>"><%=name%></a>
						<% } %>
						</li>
					<% } %>
					</ul>
				</div>
				<%
				boolean haveManus = prodMgr.getCurrentManufacturersLength() > 0;
				boolean havePriceFacets = prodMgr.getPriceFacets() != null && prodMgr.getPriceFacets().length > 0;
				boolean havePriceSlider = prodMgr.getMinPrice() != null && prodMgr.getMaxPrice() != null && prodMgr.getMinPrice().compareTo(prodMgr.getMaxPrice()) != 0;
				boolean haveFacets = prodMgr.getCurrentTagGroups() != null && prodMgr.getCurrentTagGroups().length > 0;				
				%>
				<%if (haveManus || havePriceFacets || havePriceSlider || haveFacets){%>
					<h1>
						<kk:msg  key="facet.tile.refine.search"/>	
					</h1>
				<%}%>
				<%if (prodMgr.getNumSelectedFilters() > 0 || prodMgr.isPriceFilter()){%>					
					<div id="remove-all"><img  src="<%=kkEng.getImageBase()%>/x-button.png"><a href="RemoveTags.action?t=<%=prodMgr.getProdTimestamp()%>"><kk:msg  key="products.body.clear.filters"/></a></div>				
				<%}%>
				    						
				<%if (haveManus){ %>
					<div class="side-menu-section">
						<h2><kk:msg  key="facet.tile.manufacturers"/></h2>	
						<ul>				
							<% for (int i = 0; i < prodMgr.getCurrentManufacturers().length; i++){ %>
								<% com.konakart.appif.ManufacturerIf manu = prodMgr.getCurrentManufacturers()[i];%>
								<%String name = manu.getName()+" ("+manu.getNumberOfProducts()+")"; %>
								<%if ( manu.isSelected()) { %>
									<li><a href="FilterSearchByManufacturer.action?manuId=<%=manu.getId()%>&t=<%=prodMgr.getProdTimestamp()%>"><span class="selected"></span><%=name%></a></li>	
								<% } else { %>
									<li><a href="FilterSearchByManufacturer.action?manuId=<%=manu.getId()%>&t=<%=prodMgr.getProdTimestamp()%>"><span class="not-selected"></span><%=name%></a></li>	
								<% } %>
							<% } %>
						</ul>
					</div>
				<% } %>				
				<%if (havePriceFacets){%>
					<div class="side-menu-section">
						<h2><kk:msg  key="facet.tile.price.range"/></h2>	
						<ul>				
							<% for (int i = 0; i < prodMgr.getPriceFacets().length; i++){ %>
								<% com.konakart.appif.KKPriceFacetIf pf = prodMgr.getPriceFacets()[i];%>
								<%String name = pf.getLowerLimit()+" - "+pf.getUpperLimit() + " ("+pf.getNumProds()+")"; %>
								<%if (pf.isSelected()) { %>
									<li><a href="FilterSearchByPrice.action?from=<%=pf.getLowerLimit()%>&to=<%=pf.getUpperLimit()%>&t=<%=prodMgr.getProdTimestamp()%>"><span class="selected"></span><%=name%></a></li>
								<% } else { %>
									<li><a href="FilterSearchByPrice.action?from=<%=pf.getLowerLimit()%>&to=<%=pf.getUpperLimit()%>&t=<%=prodMgr.getProdTimestamp()%>"><span class="not-selected"></span><%=name%></a></li>
								<% } %>
							<% } %>
						</ul>
						<form action="FilterSearchByPrice.action" id='priceFilterForm' method="post">
							<input id="priceFromStr" name="priceFromStr" type="hidden"/>
							<input id="priceToStr" name="priceToStr" type="hidden"/>
							<input id="timestamp" name="timestamp" type="hidden" value="<%=prodMgr.getProdTimestamp()%>"/>
						</form>
					</div>
				<% } else if (havePriceSlider){%>
					<div id="price" class="range-slider">
		    			<h2><kk:msg  key="common.price"/></h2>
						<div id="price-range-slider"></div>
						<div id="amount"></div>
	    			</div>
					<form action="FilterSearchByPrice.action" id='priceFilterForm' method="post">
						<input id="priceFromStr" name="priceFromStr" type="hidden"/>
						<input id="priceToStr" name="priceToStr" type="hidden"/>
						<input id="timestamp" name="timestamp" type="hidden" value="<%=prodMgr.getProdTimestamp()%>"/>
					</form>
				<% } %>
				<%if (haveFacets){ %>
					<div class="side-menu-section">
					<% for (int i = 0; i < prodMgr.getCurrentTagGroups().length; i++){ %>
						<% com.konakart.appif.TagGroupIf tagGroup = prodMgr.getCurrentTagGroups()[i];%>
						<h2><%=tagGroup.getName()%></h2>
						<%if (tagGroup.getTags() != null && tagGroup.getTags().length > 0){ %>
							<ul>
							<% for (int j = 0; j < tagGroup.getTags().length; j++){ %>
								<% com.konakart.appif.TagIf tag = tagGroup.getTags()[j];%>
								<%String name = (useSolr)? kkEng.getMsg(tag.getName()): tag.getName(); %>
								<%if ( tag.isSelected()) { %>
									<li><a href="FilterSearchByTags.action?tagId=<%=tag.getId()%>&t=<%=prodMgr.getProdTimestamp()%>"><span class="selected"></span><%=name+" ("+tag.getNumProducts()+")"%></a></li>
								<% } else { %>
									<li><a href="FilterSearchByTags.action?tagId=<%=tag.getId()%>&t=<%=prodMgr.getProdTimestamp()%>"><span class="not-selected"></span><%=name+" ("+tag.getNumProducts()+")"%></a></li>
								<% } %>
							<% } %>
							</ul>
						<% } %>
					<% } %>	
					</div>
				<% } %>
			<%}%>		    																											
	</div>
 
