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

<% com.konakart.al.KKAppEng kkEng = (com.konakart.al.KKAppEng) session.getAttribute("konakartKey");  %>
<% com.konakart.al.OrderMgr orderMgr = kkEng.getOrderMgr();%>
<% com.konakart.appif.OrderIf order = orderMgr.getSelectedOrder(); %>

   		
    		<h1 id="page-title"><kk:msg  key="show.order.details.body.orderinformation" arg0="<%=String.valueOf(order.getId())%>" arg1="<%=kkEng.getDateAsString(order.getDatePurchased())%>"/></h1>
	    		<div id="order-details" class="content-area rounded-corners">
		    			<div id="order-confirmation-column-left">
		    				<div id="delivery-address" class="order-confirmation-area">
			    				<div class="heading-container">
			    					<h3><kk:msg  key="show.order.details.body.deliveryaddress"/></h3>
			    				</div>
			    				<div class="order-confirmation-area-content">
				    				<span id="formattedDeliveryAddr"><%=kkEng.removeCData(order.getDeliveryFormattedAddress())%></span>
									<div id="shipping-info-view" class="order-confirmation-area-content-select">
										<label><kk:msg  key="show.order.details.body.shippingmethod"/></label>
										<p><%=order.getShippingMethod()%></p>
									</div>
								</div>		    				
			    			</div>
			    			<div id="billing-address" class="order-confirmation-area">
			    				<div class="heading-container">
			    					<h3><kk:msg  key="show.order.details.body.billingaddress"/></h3>
			    				</div>
			    				<div class="order-confirmation-area-content">
			    					<span id="formattedBillingAddr"><%=kkEng.removeCData(order.getBillingFormattedAddress())%></span>
									<div id="payment-method-view" class="order-confirmation-area-content-select">
										<label><kk:msg  key="show.order.details.body.paymentmethod"/></label>
										<p><%=order.getPaymentMethod()%></p>
									</div>
								</div>		    				
			    			</div>
			    			<div id="status-notes" class="order-confirmation-area">
			    				<div class="heading-container">
			    					<h3><kk:msg  key="show.order.details.body.orderhistory"/></h3>
			    				</div>
			    				<div class="order-confirmation-area-content">
								<%if (order.getStatusTrail() != null && order.getStatusTrail().length > 0){ %>
									<table>
									<% for (int i = 0; i < order.getStatusTrail().length; i++){ %>
										<% com.konakart.appif.OrderStatusHistoryIf ost = order.getStatusTrail()[i];%>										
										<tr>
											<td><%=kkEng.getDateTimeAsString(ost.getDateAdded())%></td>
											<td><%=ost.getOrderStatus()%></td>
											<%if (ost.getComments() == null || ost.getComments().length() == 0){ %>
												<td class="comment-col">&nbsp;</td>
											<%}else{%>
												<td class="comment-col"><%=ost.getComments()%></td>
											<%}%>
										</tr>
									<%}%>
									</table>
								<%}%>									
								</div>		    				
			    			</div>
		    			</div>
		    			<div id="order-confirmation-column-right">
			    			<div id="shopping-cart">
			    				<div class="heading-container">
			    					<h3><kk:msg  key="show.order.details.body.details"/></h3>
			    				</div>
			    				<table>
			    					<thead>
			    						<tr>
			    							<td class="wide-col"><kk:msg  key="common.item"/></td>
			    							<td class="narrow-col right"><kk:msg  key="common.tax"/></td>
			    							<td class="narrow-col right"><kk:msg  key="common.total"/></td>
			    							<td></td>
			    						</tr>
			    					</thead>
			    					<tbody id="ot-table">
		   								<%if (order.getOrderProducts() != null && order.getOrderProducts().length > 0){ %>
											<% for (int i = 0; i < order.getOrderProducts().length; i++){ %>
												<% com.konakart.appif.OrderProductIf op = order.getOrderProducts()[i];%>
												<tr>
													<td>
					    								<a href="SelectProd.action?prodId=<%=op.getProductId()%>"  class="text-link"><%=op.getName()%>
															<%if (op.getOpts() != null && op.getOpts().length > 0){ %>
																<% for (int l = 0; l < op.getOpts().length; l++){ %>
																	<% com.konakart.appif.OptionIf opt = op.getOpts()[l];%>
																	<%if (opt.getType() == com.konakart.app.Option.TYPE_VARIABLE_QUANTITY){ %>
																		<br><span class="shopping-cart-item-option"> - <%=opt.getName()%>  <%=opt.getQuantity()%> <%=opt.getValue()%></span>
																	<% } else { %>
																		<br><span class="shopping-cart-item-option"> - <%=opt.getName()%> <%=opt.getValue()%></span>
																	<% } %>
																<% } %>																								
															<% } %>
														</a>
														<div class="item-quantity"><kk:msg  key="common.quantity"/>: <%=op.getQuantity()%></div>
													</td>
													<td class="right"><%=op.getTaxRate().setScale(1, java.math.BigDecimal.ROUND_HALF_UP)%>%</td>											
													<%if (kkEng.displayPriceWithTax()) {%>
														<td  class="total-price right"><%=kkEng.formatPrice(op.getFinalPriceIncTax())%></td>
													<%} else {%>
														<td  class="total-price right"><%=kkEng.formatPrice(op.getFinalPriceExTax())%></td>
													<%}%>	
												</tr>
											<%}%>
										<%}%>
										<%if (order.getOrderTotals() != null && order.getOrderTotals().length > 0){ %>
											<% for (int i = 0; i < order.getOrderTotals().length; i++){ %>
												<% com.konakart.appif.OrderTotalIf ot = order.getOrderTotals()[i];%>
												<%String rowClass = "costs-and-promotions";%>
												<%if (ot.getClassName().equals("ot_total")){ %>
													<%rowClass = "shopping-cart-total";%>
												<% } %>										
												<tr class="<%=rowClass%>">															
													<%if (ot.getClassName().equals("ot_reward_points")){%>
													    <td class="cost-overview"><%=ot.getTitle()%></td>	
													    <td></td>
														<td class="cost-overview-amounts right"><%=ot.getValue()%></td>
													<%}else if (ot.getClassName().equals("ot_free_product")) {%>
														<td class="cost-overview"><%=ot.getTitle()%></td>
														<td></td>
														<td class="cost-overview-amounts right"><%=ot.getText()%></td>
													<%}else if (ot.getClassName().equals("ot_total")) {%>
														<td colspan="2"><%=ot.getTitle()%></td>
														<td class="right"><%=ot.getText()%></td>
													<%}else if (ot.getClassName().equals("ot_product_discount") || ot.getClassName().equals("ot_total_discount")) {%>
													    <td class="cost-overview"><span class="discount"><%=ot.getTitle()%></span></td>
													    <td></td>
														<td class="cost-overview-amounts right"><span class="discount"><%=kkEng.formatPrice(ot.getValue())%></span></td>
													<%}else{%>
													    <td class="cost-overview"><%=ot.getTitle()%></td>	
													    <td></td>
														<td class="cost-overview-amounts right"><%=kkEng.formatPrice(ot.getValue())%></td>
													<%}%>		    																		
												</tr>
											<%}%>
										<%}%>
			    					</tbody>	    				
	    						</table>
							</div>
						</div>			    				
						<div class="form-buttons-wide">
							<a href="RepeatOrder.action?orderId=<%=order.getId()%>" id="continue-button" class="button small-rounded-corners"><span><kk:msg  key="common.repeat"/></span></a>
							<a href="MyAccount.action" id="back-button" class="button small-rounded-corners"><span><kk:msg  key="common.back"/></span></a>
						</div>
	    		</div>






