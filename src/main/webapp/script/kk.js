/*
 * Sends an AJAX request to a struts action
 */
function callAction(parms, callback, url) {

	$.ajax({
		type : 'POST',
		timeout : '20000',
		scriptCharset : "utf-8",
		contentType : "application/json; charset=utf-8",
		url : url,
		data : parms,
		success : callback,
		error : function(jqXHR, textStatus, errorThrown) {
			var errorMsg = "JSON API call to the URL " + url
					+ " wasn't successful.";
			if (textStatus != null && textStatus != '') {
				errorMsg += "\nStatus:\t" + textStatus;
			}
			if (errorThrown != null && errorThrown != '') {
				errorMsg += "\nError:\t" + errorThrown;
			}
			alert(errorMsg);
		},
		dataType : 'json'
	});
}

/*
 * Suggested search code used in Header.jsp. Figure out which search to do based
 * on value in key.
 */
function kkSearch() {

	// Get key and search string from page
	var key = document.getElementById('kk_key').value;
	var text = document.getElementById('search-input').value;

	if (key != null && key.length > 0) {
		var keyArray = key.split(',');
		if (keyArray.length == 3) {
			var manuId = keyArray[1];
			var catId = keyArray[2];
			if (catId > -1 && manuId > -1) {
				// Search category and manufacturer
				document.getElementById('manuId').value = manuId;
				document.getElementById('catId').value = catId;
				document.getElementById('ssForm').action = "SelectCat.action";
				document.getElementById('ssForm').submit();
			} else if (catId > -1) {
				// Search cat
				document.getElementById('manuId').value = "-1";
				document.getElementById('catId').value = catId;
				document.getElementById('ssForm').action = "SelectCat.action";
				document.getElementById('ssForm').submit();
			} else if (manuId > -1) {
				// Search manufacturer
				document.getElementById('manuId').value = manuId;
				document.getElementById('ssForm').action = "ShowSearchByManufacturerResultsByLink.action";
				document.getElementById('ssForm').submit();
			} else {
				// Search based on text
				document.getElementById('searchText').value = text;
				document.getElementById('ssForm').action = "QuickSearch.action";
				document.getElementById('ssForm').submit();
			}
		}
	} else if (text != null && text.length > 0) {
		/*
		 * Reach here if someone has entered free text and clicked the search
		 * button or the enter key. Rather than doing a search on the text we
		 * see if there is a suggested search hit and then use the extra
		 * information returned from the suggested search hit to provide better
		 * results. i.e. It provides results for a category search whereas a
		 * simple search wouldn't show any results.
		 */
		callAction('{"term":"' + text + '"}', suggestedSearchCallback,
				"SuggestedSearch.action");
	}
}

/*
 * Callback for suggested search
 */
var suggestedSearchCallback = function(result, textStatus, jqXHR) {
	if (result != null && result.length > 0) {
		document.getElementById('kk_key').value = result[0].id;
		document.getElementById('search-input').value = result[0].value;
		kkSearch();
	} else {
		var text = document.getElementById('search-input').value;
		document.getElementById('searchText').value = text;
		document.getElementById('ssForm').action = "QuickSearch.action";
		document.getElementById('ssForm').submit();
	}
};

/*
 * Reset key id since user has typed into search box
 */
function kkKeydown() {
	document.getElementById('kk_key').value = "";
}

/*
 * Used by address maintenance panels
 */
function changeCountry() {
	if (document.getElementById('state')) {
		document.getElementById('state').value="";
	}	
	document.getElementById('countryChange').value="1";
	document.getElementById('form1').submit();
}

$(function() {
	
	$("#shopping-cart").click(goToCartPage);
	$("#wish-list").click(goToWishListPage);
	
	$(".item-over").click(function() {
		var prodId = (this.id).split('-')[1];
		goToProdDetailsPage(prodId);
	});	
	
	/*
	 * Hover effects for Add To Cart button
	 */	
	$(".item").not(".style-small").hover(
			function() {
				$(this).addClass("item-over-container");
				$(this).find(".item-over").show();
			}, function() {
				$(this).removeClass("item-over-container");
				$(this).find(".item-over").hide();
			});

	
	/*
	 * Hover effects for Sliding Cart 
	 */
	var cartHover=0;
	$("#shopping-cart").hover(
			function() {
				// in
				cartHover=1;
				showCart("#shopping-cart");
			}, function() {				
				// out
				setTimeout(function(){
					if (cartHover!=2) {
						cartHover=0;
						hideCart("#shopping-cart");
					}
					}, 500);
			});
	$("#shopping-cart-container").hover(
			function() {
				// in
				cartHover=2;
				showCart("#shopping-cart");		
			}, function() {
				// out
				cartHover=0;
				hideCart("#shopping-cart");
			});
	
	/*
	 * Initialise wish list position and visibility 
	 */
	setWishListPosition();
		
	/*
	 * Hover effects for Sliding Wish list 
	 */
	var wlHover=0;
	$("#wish-list").hover(
			function() {
				// in
				wlHover=1;
				showWishList("#wish-list");
			}, function() {				
				// out
				setTimeout(function(){
					if (wlHover!=2) {
						wlHover=0;
						hideWishList("#wish-list");
					}
					}, 500);				
			});
	$("#wish-list-container").hover(
			function() {
				// in
				wlHover=2;
				showWishList("#wish-list");		
			}, function() {
				// out
				wlHover=0;
				hideWishList("#wish-list");
			});
	/*
	 * Add to Cart
	 */
	$(".add-to-cart-button")
	.click(
			function() {
				var prodId = (this.id).split('-')[1];
				callAction('{"prodId":"' + prodId + '"}', 
						addToCartCallback,
						"AddToCartFromProdId.action");
				return false;
			});
	
	/*
	 * Add to Wish List
	 */
	$(".add-to-wishlist")
			.click(
					function() {
						var prodId = (this.id).split('-')[1];
						callAction('{"prodId":"' + prodId + '"}', 
								 addToWishListCallback,
								"AddToWishListFromProdId.action");
						return false;
					});
	
	/*
	 * Subscribe to newslette
	 */
	$("#newsletter-button").click(submitNewsletterForm);
	
	/*
	 * Basket checkout button on fade in / out basket widget
	 */
	$("#shopping-cart-checkout-button").click(goToCheckoutPage);
	
	/*
	 * Tooltips
	 */
	$(".has-tooltip").tooltip();

});

/*
 * Submits the sign up to newsletter form
 */
function submitNewsletterForm() {
	var email = $("#newsletter-input").val();
	callAction('{"emailAddr":"' + email + '"}', 
			 subscribeNewsletterCallback,
			"SubscribeNewsletter.action");
	return false;
}

/*
 * Set the position of the wish list slide down control
 */
function setWishListPosition() {
	if ($("#wish-list").length) {		
		$("#wish-list-container").hide();
		var shadowWidth  =  $("#wish-list-mouseover-shadow").width();
		var space = $("#shopping-cart").position().left - $("#wish-list").position().left-$("#wish-list").width();
		var cartWidth = $("#shopping-cart").width();
		$("#wish-list-mouseover-shadow").css("right", cartWidth+space/2-shadowWidth);
		$("#wish-list-contents").css("right", cartWidth+space/2);	
	}
}

/*
 * Redirect functions
 */
function goToCartPage() {
	return redirect("ShowCartItems.action");
}

function goToCheckoutPage() {
	return redirect("Checkout.action");
}

function goToWishListPage() {
	return redirect("ShowWishListItems.action");
}

function goToProdDetailsPage(prodId){
	return redirect("SelectProd.action?prodId="+prodId);
}

function redirect(action) {
	var parts = window.location.pathname.split('/');
	var redirectUrl = "";
	for ( var i = 0; i < parts.length - 1; i++) {
		var part = parts[i];
		redirectUrl += part + "/";
	}
	redirectUrl += action;
	window.location = redirectUrl;
	return false;
}



/*
 * Code to display the slide out cart
 */
function showCart(cart) {
	$(cart).addClass("small-rounded-corners-top shopping-cart-mouseover");
	$("#shopping-cart-container").css("display","inline");
}

/*
 * Code to hide the slide out cart
 */
function hideCart(cart) {
	$("#shopping-cart-container").hide();
	$(cart).removeClass("shopping-cart-mouseover small-rounded-corners-top");
}

/*
 * Code to display the slide out wish list
 */
function showWishList(wishList) {
	$(wishList).addClass("small-rounded-corners-top shopping-cart-mouseover");	
	$("#wish-list-container").css("display","inline");
}

/*
 * Code to hide the slide out wish list
 */
function hideWishList(wishList) {
	$("#wish-list-container").hide();
	$(wishList).removeClass("small-rounded-corners-top shopping-cart-mouseover");
}

/*
 * Calculate the product image base
 */
function getProdImageBase(prod, base) {
	return base + prod.imageDir + prod.uuid;
}

/*
 * Used to view added to cart details in a popup
 */
var addToCartCallback = function(result, textStatus, jqXHR) {

	/*
	 * Go to product details page to choose options
	 */
	if (result.redirectURL != null) {
		window.location = result.redirectURL;
		return;
	}

	var txt;
	/*
	 * Update cart slide-out with new basket items
	 */		
	if (result.items != null && result.items.length > 0) {
		txt = '<div id="shopping-cart-items">';
		for ( var i = 0; i < result.items.length; i++) {
			var item = result.items[i];
			txt += '<div class="shopping-cart-item">';
			txt += '<a href="SelectProd.action?prodId='+item.product.id+'"><img src="'+getProdImageBase(item.product,result.imgBase)+'_1_tiny.jpg'+'" border="0" alt="'+item.product.name+'" title="'+item.product.name+'"></a>';
			txt += '<a href="SelectProd.action?prodId='+item.product.id+'" class="shopping-cart-item-title">'+item.product.name+'</a>';
			if (item.opts != null && item.opts.length > 0) {
				for ( var j = 0; j < item.opts.length; j++) {
					var opt = item.opts[j];
					if (opt.type == 1) {
						txt += '<br><span class="shopping-cart-item-option"> - '+opt.name+' '+opt.quantity+' '+opt.value+'</span>';
					} else {
						txt += '<br><span class="shopping-cart-item-option"> - '+opt.name+' '+opt.value+'</span>';
					}					
				}
			}
			txt += '<div class="shopping-cart-item-price">';
			txt += result.formattedPrices[i];
			txt += ' '+result.quantityMsg+': '+item.quantity;
			txt += '</div>';
			txt += '</div>';
		}
		txt += '</div>';
		txt += '<div id="subtotal-and-checkout">';
		txt += '<div class="subtotal">';
		txt += '<div class="subtotal-label">'+result.subtotalMsg+'</div>';
		txt += '<div class="subtotal-amount">'+result.basketTotal+'</div>';
		txt += '<div id="shopping-cart-checkout-button" class="button small-rounded-corners">'+result.checkoutMsg+'</div>';
		txt += '</div>';
		txt += '</div>';
	} else {
		txt = result.emptyCartMsg;
	}
	$("#shopping-cart-contents").html(txt);
	
	/*
	 * Set event code on checkout button
	 */
	$("#shopping-cart-checkout-button").click(goToCheckoutPage);
	
	/*
	 * Update cart summary with new basket data
	 */
	txt = result.shoppingCartMsg;
	if (result.numberOfItems > 0) {
		txt += " ("+result.numberOfItems+")";
	} 
	$("#shopping-cart").html(txt);

	/*
	 * Reset the position of the wish list slide out control since
	 * the cart summary length may have changed
	 */
	setWishListPosition();

	/*
	 * Display cart to show that something has been added
	 */
	showCart("#shopping-cart");
	window.setTimeout("hideCart('#shopping-cart')", 2000);
};

/*
 * Used to update the wish list
 */
var addToWishListCallback = function(result, textStatus, jqXHR) {

	/*
	 * Go to product details page to choose options
	 */
	if (result.redirectURL != null) {
		window.location = result.redirectURL;
		return;
	}

	/*
	 * Update wish list slide-out with new wlItems
	 */		
	if (result.wlItems != null && result.wlItems.length > 0) {
		txt = '<div id="wish-list-items">';
		for ( var i = 0; i < result.wlItems.length; i++) {
			var item = result.wlItems[i];
			txt += '<div class="shopping-cart-item">';
			txt += '<a href="SelectProd.action?prodId='+item.product.id+'"><img src="'+getProdImageBase(item.product,result.imgBase)+'_1_tiny.jpg'+'" border="0" alt="'+item.product.name+'" title="'+item.product.name+'"></a>';
			txt += '<a href="SelectProd.action?prodId='+item.product.id+'" class="shopping-cart-item-title">'+item.product.name+'</a>';
			if (item.opts != null && item.opts.length > 0) {
				for ( var j = 0; j < item.opts.length; j++) {
					var opt = item.opts[j];
					if (opt.type == 1) {
						txt += '<br><span class="shopping-cart-item-option"> - '+opt.name+' '+opt.quantity+' '+opt.value+'</span>';
					} else {
						txt += '<br><span class="shopping-cart-item-option"> - '+opt.name+' '+opt.value+'</span>';
					}					
				}
			}
			txt += '<div class="shopping-cart-item-price">';
			txt += result.formattedPrices[i];
			txt += '</div>';
			txt += '</div>';
		}
		txt += '</div>';
		txt += '<div id="wish-list-subtotal">';
		txt += '<div class="subtotal">';
		txt += '<div class="subtotal-label">'+result.subtotalMsg+'</div>';
		txt += '<div class="subtotal-amount">'+result.wishListTotal+'</div>';
		txt += '</div>';
		txt += '</div>';
	} else {
		txt = result.emptyWishListMsg;
	}	
	$("#wish-list-contents").html(txt);
	
	/*
	 * Update wish liat summary with new number of wlItems
	 */
	var txt = result.wishListMsg;
	if (result.numberOfItems > 0) {
		txt += " ("+result.numberOfItems+")";
	} 
	$("#wish-list").html(txt);
	
	/*
	 * Display wish liat to show that something has been added
	 */
	showWishList("#wish-list");
	window.setTimeout("hideWishList('#wish-list')", 2000);

};

/*
 * Newsletter subscription callback
 */
var subscribeNewsletterCallback = function(result, textStatus, jqXHR) {

	if (result.msg != null) {
		$("#newsletter-msg").html(result.msg);

		if (result.error==true) {
			$("#newsletter-msg").removeClass("messageStackSuccess");
			$("#newsletter-msg").addClass("messageStackError");
		} else {
			$("#newsletter-msg").removeClass("messageStackError");
			$("#newsletter-msg").addClass("messageStackSuccess");
		}
	}
};