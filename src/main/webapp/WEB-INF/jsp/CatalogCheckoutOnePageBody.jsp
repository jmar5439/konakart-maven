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
<% com.konakart.appif.OrderIf order = orderMgr.getCheckoutOrder();%>
<% com.konakart.al.RewardPointMgr rewardPointMgr = kkEng.getRewardPointMgr();%>
<% com.konakart.al.CustomerMgr customerMgr = kkEng.getCustomerMgr();%>
<% com.konakart.appif.CustomerIf cust = customerMgr.getCurrentCustomer();%>


<script type="text/javascript">	

var onePageRefreshCallback = function(result, textStatus, jqXHR) {
	if (result.timeout != null) {
		alert('<%=kkEng.getMsg("common.session.timeout")%>');
		document.getElementById('form1').submit();
	}else{		
		$("#ot-table").empty();
		if (result.opArray != null && result.opArray.length > 0) {
			for ( var i = 0; i < result.opArray.length; i++) {
				var op = result.opArray[i];
				var row;
				
			 	row = '<tr><td>';
			 	row +='<a href="SelectProd.action?prodId='+op.productId+'"  class="text-link">'+op.name;
			   	if (op.opts != null) {
					for (var k = 0; k < op.opts.length; k++) {
						var opt = op.opts[k];
						if (opt != null) {
							if (opt.type == 1) {
								row += '<br><span class="shopping-cart-item-option"> - ';
								row += opt.name;
								row += ': ';
								row += opt.quantity;
								row += ' ';
								row += opt.value;
								row += '</class>';
							} else {
								row += '<br><span class="shopping-cart-item-option"> - ';
								row += opt.name;
								row += ': ';
								row += opt.value;
								row += '</class>';
							}							
						}						
					}
				}				 	
			   	row +='</a>';
			   	row +='<div class="item-quantity">'+result.qtyMsg+':'+ op.quantity+'</div>';				 	
			   	row += '</td>';
			   	row += '<td class="right">'+op.formattedTaxRate+'%</td>';	
				if (result.displayPriceWithTax) {
					row += '<td  class="total-price right">';
					row += op.formattedFinalPriceIncTax;
					row += '</td>';
				} else {
					row += '<td  class="total-price right">';
					row += op.formattedFinalPriceExTax;
					row += '</td>';
				}
				row += '</tr>';					
				$("#ot-table").append(row);				
			}	
		}	
		
		if (result.otArray != null && result.otArray.length > 0) {
			for ( var i = 0; i < result.otArray.length; i++) {
				var ot = result.otArray[i];
				var row;
				var rowClass = "costs-and-promotions";
				if (ot.className == "ot_total") rowClass = "shopping-cart-total";
				row = '<tr class="'+rowClass+'">';
				if (ot.className == "ot_reward_points") {
					row += '<td class="cost-overview">'+ot.title+'</td>';	
					row += '<td></td>';
					row += '<td class="cost-overview-amounts right">'+ot.value+'</td>';
				} else if (ot.className == "ot_free_product") {
					row += '<td class="cost-overview">'+ot.title+'</td>';	
					row += '<td></td>';
					row += '<td class="cost-overview-amounts right">'+ot.text+'</td>';
				} else if (ot.className == "ot_total") {
					row += '<td colspan="2">'+ot.title+'</td>';	
					row += '<td class="right">'+ot.text+'</td>';
				} else if (ot.className == "ot_product_discount" || ot.className == "ot_total_discount") {
					row += '<td class="cost-overview"><span class="discount">'+ot.title+'</span></td>';	
					row += '<td></td>';
					row += '<td class="cost-overview-amounts right"><span class="discount">'+ot.text+'</span></td>';
				} else  {
					row += '<td class="cost-overview">'+ot.title+'</td>';	
					row += '<td></td>';
					row += '<td class="cost-overview-amounts right">'+ot.text+'</td>';
				}
				row += '</tr>';
				$("#ot-table").append(row);				
			}	
		}
		
		if (result.formattedDeliveryAddr != null) {
			$("#formattedDeliveryAddr").html(result.formattedDeliveryAddr);
			$("#editDelivery").attr("href", "EditAddr.action?addrId="+result.deliveryAddrId+"&opcdelivery=true");
		}	
		if (result.formattedBillingAddr != null) {
			$("#formattedBillingAddr").html(result.formattedBillingAddr);
			$("#editBilling").attr("href", "EditAddr.action?addrId="+result.billingAddrId+"&opcbilling=true");
		}
		
		if ($("#couponCodeUpdate").length) $('#couponCodeUpdate').hide();
		if ($("#giftCertCodeUpdate").length) $('#giftCertCodeUpdate').hide();
		if ($("#rewardPointsUpdate").length) $('#rewardPointsUpdate').hide();
	}
};

function shippingRefresh() {	
	var quotes = document.getElementById("shippingQuotes");
	var selectedQuote = quotes.options[quotes.selectedIndex].value;
	callAction('{"shipping":"'+selectedQuote+'"}', onePageRefreshCallback, "OnePageRefresh.action");
	setLoading();
}

function paymentRefresh() {	
	var paymentDetails = document.getElementById("paymentDetails");
	var selectedPayment = paymentDetails.options[paymentDetails.selectedIndex].value;
	callAction('{"payment":"'+selectedPayment+'"}', onePageRefreshCallback, "OnePageRefresh.action");
	setLoading();
}

function couponCodeRefresh() {	
	var val = document.getElementById("couponCode").value;
	callAction('{"couponCode":"'+val+'"}', onePageRefreshCallback, "OnePageRefresh.action");
	setLoading();
}

function giftCertCodeRefresh() {	
	var val = document.getElementById("giftCertCode").value;
	callAction('{"giftCertCode":"'+val+'"}', onePageRefreshCallback, "OnePageRefresh.action");
	setLoading();
}

function rewardPointsRefresh() {	
	var val = document.getElementById("rewardPoints").value;
	callAction('{"rewardPoints":"'+val+'"}', onePageRefreshCallback, "OnePageRefresh.action");
	setLoading();
}

function setLoading() {
	$("#ot-table").empty().append('<tr><td colspan="3" class="loading"></td></tr>');	
}

var deliveryAddr = true;

function selectAddr(id) {	
	$("#addr-dialog").dialog('close');
	if (deliveryAddr) {
		callAction('{"deliveryAddrId":"'+id+'"}', onePageRefreshCallback, "OnePageRefresh.action");
	} else {
		callAction('{"billingAddrId":"'+id+'"}', onePageRefreshCallback, "OnePageRefresh.action");
	}
	setLoading();
}


$(function() {
	
    if ($("#form1").length) {
		$("#form1").validate(validationRules);
	}
	
	$("#addr-dialog").dialog({
		autoOpen: false,
		width: "500",
		modal: "true",
		hide: "blind"
	});
	
	$('#couponCode').keyup(function() {
		  var elem = $(this);
		  var val = elem.valid();
		  if (val==1 || elem.val()=="") {
			  $('#couponCodeUpdate').show();
		  } else {
			  $('#couponCodeUpdate').hide();
		  }
	});
	

	$('#giftCertCode').keyup(function() {
		  var elem = $(this);
		  var val = elem.valid();
		  if (val==1 || elem.val()=="") {
			  $('#giftCertCodeUpdate').show();
		  } else {
			  $('#giftCertCodeUpdate').hide();
		  }
	});


	$('#rewardPoints').keyup(function() {
		  var elem = $(this);
		  var val = elem.valid();
		  if (val==1 || elem.val()=="") {
			  $('#rewardPointsUpdate').show();
		  } else {
			  $('#rewardPointsUpdate').hide();
		  }
	});

	$("#abdelivery").click(function() {
		deliveryAddr = true;
		$("#addr-dialog").dialog( "open" );
		return false;
	});
	
	$("#abshipping").click(function() {
		deliveryAddr = false;
		$("#addr-dialog").dialog( "open" );
		return false;
	});
	
});

</script>

    		
 	    	<div id="addr-dialog" title="<span><kk:msg  key="header.address.book"/>" class="content-area rounded-corners">
	    		<div>
					<div class="form-section">
						<div class="form-section-title no-margin">
							<h3><kk:msg  key="address.book.dialog.select"/></h3>									
						</div>
						<%if (cust.getAddresses() != null && cust.getAddresses().length > 0){ %>
							<% for (int i = 0; i < cust.getAddresses().length; i++){ %>
								<% com.konakart.appif.AddressIf addr = cust.getAddresses()[i];%>						
								<div class="select-addr-section <%=(i%2==0)?"even":"odd"%>">
									<div class="select-addr">
										<%=kkEng.removeCData(addr.getFormattedAddress())%>
									</div>
									<div class="select-addr-buttons">
										<a onclick="selectAddr(<%=addr.getId()%>);" class="button small-rounded-corners">
											<span ><kk:msg  key="common.select"/></span>
										</a>									
									</div>
								</div>
							<%}%>
						<%}%>
					</div>
		    	</div>
		    </div>


    		<h1 id="page-title"><kk:msg  key="checkout.confirmation.orderconfirmation"/></h1>
	    		<div id="order-confirmation" class="content-area rounded-corners">
		    		<form action="CheckoutConfirmationSubmit.action" id="form1" method="post" class="form-section">
		    			<div id="order-confirmation-column-left">
		    				<div id="delivery-address" class="order-confirmation-area">
			    				<div class="heading-container">
			    					<h3><kk:msg  key="show.order.details.body.deliveryaddress"/></h3>
			    					<div class="order-confirmation-options">
			    					<a href="NewAddr.action?opcdelivery=true" title="<kk:msg  key="checkout.confirmation.new.addr.tip"/>" class="order-confirmation-option text-link has-tooltip"><kk:msg  key="common.new"/></a>
			    					<span class="separator-small"></span>
			    					<a id="editDelivery" href="EditAddr.action?addrId=<%=order.getDeliveryAddrId()%>&opcdelivery=true" title="<kk:msg  key="checkout.confirmation.edit.addr.tip"/>" class="order-confirmation-option text-link has-tooltip"><kk:msg  key="common.edit"/></a>
									<%if (cust != null && cust.getType() != 2) { %>
				    					<span class="separator-small"></span>
				    					<a id="abdelivery" title="<kk:msg  key="checkout.confirmation.addr.book.tip"/>" class="order-confirmation-option text-link has-tooltip"><kk:msg  key="checkout.confirmation.addr.book"/></a>
									<% } %>
			    				</div>
			    				</div>
			    				<div class="order-confirmation-area-content">
				    				<span id="formattedDeliveryAddr"><%=kkEng.removeCData(order.getDeliveryFormattedAddress())%></span>
									<div id="shipping-info" class="order-confirmation-area-content-select">
										<label><kk:msg  key="show.order.details.body.shippingmethod"/></label>
										<select name="shipping" onchange="javascript:shippingRefresh();" id="shippingQuotes">
											<%if (orderMgr.getShippingQuotes() != null && orderMgr.getShippingQuotes().length > 0){ %>										
												<s:set scope="request" var="shipping"  value="shipping"/> 						
												<% String shipping = ((String)request.getAttribute("shipping"));%> 
												<% for (int i = 0; i < orderMgr.getShippingQuotes().length; i++){ %>
													<% com.konakart.appif.ShippingQuoteIf quote = orderMgr.getShippingQuotes()[i];%>
													<%if (shipping.equals(quote.getCode())){ %>
														<option  value="<%=quote.getCode()%>" selected="selected"><%=quote.getDescription()%></option>
													<% } else { %>
														<option  value="<%=quote.getCode()%>"><%=quote.getDescription()%></option>
													<% } %>
												<% } %>										
											<%} else {%>
												<option  value="-1" selected="selected"><kk:msg  key="one.page.checkout.no.shipping.methods"/></option>
											<% } %>
										</select>
									</div>
								</div>		    				
			    			</div>
			    			<div id="billing-address" class="order-confirmation-area">
			    				<div class="heading-container">
			    					<h3><kk:msg  key="show.order.details.body.billingaddress"/></h3>
			    					<div class="order-confirmation-options">
			    					<a href="NewAddr.action?opcbilling=true" title="<kk:msg  key="checkout.confirmation.new.addr.tip"/>" class="order-confirmation-option text-link has-tooltip"><kk:msg  key="common.new"/></a>
			    					<span class="separator-small"></span>
			    					<a id="editBilling" href="EditAddr.action?addrId=<%=order.getBillingAddrId()%>&opcbilling=true" title="<kk:msg  key="checkout.confirmation.edit.addr.tip"/>" class="order-confirmation-option text-link has-tooltip"><kk:msg  key="common.edit"/></a>
									<%if (cust != null && cust.getType() != 2) { %>
				    					<span class="separator-small"></span>
				    					<a id="abshipping" title="<kk:msg  key="checkout.confirmation.addr.book.tip"/>" class="order-confirmation-option text-link has-tooltip"><kk:msg  key="checkout.confirmation.addr.book"/></a>
									<% } %>
			    				</div>
			    				</div>
			    				<div class="order-confirmation-area-content">
			    					<span id="formattedBillingAddr"><%=kkEng.removeCData(order.getBillingFormattedAddress())%></span>
									<div id="payment-method" class="order-confirmation-area-content-select">
										<label><kk:msg  key="show.order.details.body.paymentmethod"/></label>
										<select name="payment" onchange="javascript:paymentRefresh();" id="paymentDetails">
											<%if (orderMgr.getPaymentDetailsArray() != null && orderMgr.getPaymentDetailsArray().length > 0){ %>										
												<s:set scope="request" var="payment"  value="payment"/> 						
												<% String payment = ((String)request.getAttribute("payment"));%> 
												<% for (int i = 0; i < orderMgr.getPaymentDetailsArray().length; i++){ %>
													<% com.konakart.appif.PaymentDetailsIf pd = orderMgr.getPaymentDetailsArray()[i];%>
													<%if (payment.equals(pd.getCode())){ %>
														<option  value="<%=pd.getCode()%>" selected="selected"><%=pd.getDescription()%></option>
													<% } else { %>
														<option  value="<%=pd.getCode()%>"><%=pd.getDescription()%></option>
													<% } %>
												<% } %>										
											<%} else {%>
												<option  value="-1" selected="selected"><kk:msg  key="one.page.checkout.no.payment.methods"/></option>
											<% } %>
										</select>
									</div>
									<div id="promotion-codes">
										<div id="promotion-codes-container">
									    	<%if (kkEng.getConfigAsBoolean("DISPLAY_COUPON_ENTRY",false)) { %>
									    		<div class="promotion-codes-field">				
													<label><kk:msg  key="checkout.common.couponcode"/></label>
													<input type="text" name="couponCode" id="couponCode" value="<s:property value="couponCode" />"/>
													<a id="couponCodeUpdate" class="update-button small-rounded-corners" onclick="couponCodeRefresh();" onmouseover="resetGoToCheckout()"><kk:msg  key="common.update"/></a>
													<span class="validation-msg"></span>
												</div>
											<% } %>
											<%if (kkEng.getConfigAsBoolean("DISPLAY_GIFT_CERT_ENTRY",false)) { %>
												<div class="promotion-codes-field">				
													<label><kk:msg  key="checkout.common.giftcertcode"/></label>
													<input type="text" name="giftCertCode" id="giftCertCode" value="<s:property value="giftCertCode" />"/>
													<a id="giftCertCodeUpdate" class="update-button small-rounded-corners" onclick="giftCertCodeRefresh();" onmouseover="resetGoToCheckout()"><kk:msg  key="common.update"/></a>
													<span class="validation-msg"></span>
												</div>
											<% } %>
											<%if (kkEng.getConfigAsBoolean("ENABLE_REWARD_POINTS",false)) { %>
												<%int points = rewardPointMgr.pointsAvailable(); %>
												<%if  (points > 0) { %>
													<div class="promotion-codes-field">	
														<label><kk:msg  key="checkout.common.reward_points" arg0="<%=Integer.toString(points)%>"/></label>
														<input type="text" name="rewardPoints" id="rewardPoints" value="<s:property value="rewardPoints" />"/>
														<a id="rewardPointsUpdate" class="update-button small-rounded-corners" onclick="rewardPointsRefresh();" onmouseover="resetGoToCheckout()"><kk:msg  key="common.update"/></a>
														<span class="validation-msg"></span>
													</div>
												<% } %>
											<% } %>
										</div>
		    						</div>
								</div>		    				
			    			</div>
			    			<div id="delivery-notes" class="order-confirmation-area">
			    				<div class="heading-container">
			    					<h3><kk:msg  key="checkout.common.delivery.notes"/></h3>
			    				</div>
			    				<div class="order-confirmation-area-content">
									<label><kk:msg  key="checkout.common.info"/></label> <textarea rows="5" name="comment"></textarea>
								</div>		    				
			    			</div>
		    			</div>
		    			<div id="order-confirmation-column-right">
			    			<div id="shopping-cart">
			    				<div class="heading-container">
			    					<h3><kk:msg  key="checkout.common.shopping.cart"/></h3>
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
						<div id="confirm-order-button-container">	
							<a onclick="javascript:formValidate('form1', 'continue-button');" id="continue-button" class="button small-rounded-corners">
								<span><kk:msg  key="common.confirmorder"/></span>
							</a>
						</div>
					</form>			    	
	    		</div>






