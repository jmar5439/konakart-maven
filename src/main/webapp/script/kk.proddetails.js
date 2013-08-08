$(function() {
	
	$(window).scroll(function() {
		 $.cookie('y_cookie', $(window).scrollTop(), { expires: 7, path: '/' });
	});
	
	var y = $.cookie('y_cookie');
	if (y != null && y.length > 0) {
		$(window).scrollTop(y);
	}
	
	$("#AddToCartForm").submit(function(){
		var formInput=$(this).serialize();
		if (document.getElementById('addToWishList').value=="true") {
			if (document.getElementById('wishListId').value!="-1") {
				$.getJSON('AddToCartOrWishListFromPost.action', formInput, addToGiftRegistryCallback);
			} else {
				$.getJSON('AddToCartOrWishListFromPost.action', formInput, addToWishListCallback);
			} 		
		} else {
	 		$.getJSON('AddToCartOrWishListFromPost.action', formInput, addToCartCallback);
		}
		return false;
	});

	jQuery('#related-carousel').jcarousel({
        vertical: true,
        scroll: 3,
        itemFallbackDimension: 300,
        initCallback: relatedCarousel_initCallback,
        buttonNextCallback: relatedCarousel_nextCallback,
        buttonPrevCallback: relatedCarousel_prevCallback,
        // This tells jCarousel NOT to autobuild prev/next buttons
        buttonNextHTML: null,
        buttonPrevHTML: null
    });
	
	jQuery('#also-bought-carousel').jcarousel({
        vertical: true,
        scroll: 3,
        itemFallbackDimension: 300,
        initCallback: alsoBought_initCallback,
        buttonNextCallback: alsoBought_nextCallback,
        buttonPrevCallback: alsoBought_prevCallback,
        // This tells jCarousel NOT to autobuild prev/next buttons
        buttonNextHTML: null,
        buttonPrevHTML: null
    });

	
	// Tabs
	if ($("#product-reviews-tab").attr("class").indexOf("selected-product-content-tab") >= 0) {
		$("#product-description").hide();
	} else {
		$("#product-reviews").hide();
		$(window).scrollTop(0);
	}
	$("#product-specifications").hide();
	
	$("#product-reviews-tab").click(function() {
		$("#product-description-tab").removeClass("selected-product-content-tab");
		$("#product-specifications-tab").removeClass("selected-product-content-tab");
		$("#product-reviews-tab").addClass("selected-product-content-tab");
		$("#product-description").hide();
		$("#product-specifications").hide();
		$("#product-reviews").show();
	});
	
	$("#product-specifications-tab").click(function() {
		$("#product-description-tab").removeClass("selected-product-content-tab");
		$("#product-specifications-tab").addClass("selected-product-content-tab");
		$("#product-reviews-tab").removeClass("selected-product-content-tab");
		$("#product-description").hide();
		$("#product-specifications").show();
		$("#product-reviews").hide();
	});
	
	$("#product-description-tab").click(function() {
		$("#product-description-tab").addClass("selected-product-content-tab");
		$("#product-specifications-tab").removeClass("selected-product-content-tab");
		$("#product-reviews-tab").removeClass("selected-product-content-tab");
		$("#product-description").show();
		$("#product-specifications").hide();
		$("#product-reviews").hide();
	});
		
	// Images
	var imgBase = document.getElementById('gallery_nav_base').value
				  +document.getElementById('gallery_nav_dir').value
				  +document.getElementById('gallery_nav_uuid').value;
	$("#gallery_nav").empty();
	$("#gallery_output").empty();
	var imagesSmall = "";
	var imagesLarge = "";
	for ( var i = 1; i < 11; i++) {
		var imgSrcSmall = imgBase + "_" + i + "_small.jpg";
		var imgSrcLarge = imgBase + "_" + i + "_big.jpg";
		if(imgExists(imgSrcSmall)) {
			imagesSmall += '<a rel="img' + i
			+ '" href="javascript:;"><img src="' + imgSrcSmall
			+ '"/></a>';
			imagesLarge += '<img id="img' + i + '" src="' + imgSrcLarge + '"/>';
		} else {
			break;
		}					
	}
	if (imagesLarge.length == 0) {
		var imgSrcLarge = imgBase + '/' + name + "_big." + ext;
		if(imgExists(imgSrcLarge)) {
			imagesLarge += '<img id="img' + i + '" src="' + imgSrcLarge + '"/>';
		}
	}
	
	$("#gallery_nav").append(imagesSmall);
	$("#gallery_output").append(imagesLarge);

	
	$("#gallery_output img").not(":first").hide();
	$("#gallery_output img").eq(0).addpowerzoom();

	$("#gallery a").click(function() {
		var id = "#" + this.rel;
		if ($(id).is(":hidden")) {
			$("#gallery_output img").slideUp();
			$(id).slideDown( function() {
				$(id).addpowerzoom();
			});
		}
	});	
	
});

// Carousel init
function relatedCarousel_initCallback(carousel) {
	
    jQuery('#kk-up-rc').bind('click', function() {
        carousel.next();
        return false;
    });

    jQuery('#kk-down-rc').bind('click', function() {
        carousel.prev();
        return false;
    });
};

// Up
function relatedCarousel_nextCallback(carousel,control,flag) {
    if (flag) {
    	jQuery('#kk-up-rc').addClass("next-items-up").removeClass("next-items-up-inactive");
	} else {
    	jQuery('#kk-up-rc').addClass("next-items-up-inactive").removeClass("next-items-up");
		
	}
};

// Down
function relatedCarousel_prevCallback(carousel,control,flag) {
    if (flag) {
    	jQuery('#kk-down-rc').addClass("previous-items-down").removeClass("previous-items-down-inactive");
	} else {
    	jQuery('#kk-down-rc').addClass("previous-items-down-inactive").removeClass("previous-items-down");
		
	}
};

//Carousel init
function alsoBought_initCallback(carousel) {
	
    jQuery('#kk-up-ab').bind('click', function() {
        carousel.next();
        return false;
    });

    jQuery('#kk-down-ab').bind('click', function() {
        carousel.prev();
        return false;
    });
};

// Up
function alsoBought_nextCallback(carousel,control,flag) {
    if (flag) {
    	jQuery('#kk-up-ab').addClass("next-items-up").removeClass("next-items-up-inactive");
	} else {
    	jQuery('#kk-up-ab').addClass("next-items-up-inactive").removeClass("next-items-up");
		
	}
};

// Down
function alsoBought_prevCallback(carousel,control,flag) {
    if (flag) {
    	jQuery('#kk-down-ab').addClass("previous-items-down").removeClass("previous-items-down-inactive");
	} else {
    	jQuery('#kk-down-ab').addClass("previous-items-down-inactive").removeClass("previous-items-down");
		
	}
};

function setAddToWishList() {
			document.getElementById('addToWishList').value="true";
			document.getElementById('wishListId').value="-1";
		}
	
function resetAddToWishList() {
		    document.getElementById('addToWishList').value="false";
		}

function setWishListId(id) {
	document.getElementById('wishListId').value=id;
	document.getElementById('addToWishList').value="true";
}

		
function addtoCartOrWishListFunc(){
	$("#AddToCartForm").submit();	
}

function imgExists(imgPath) {
	 var http = jQuery.ajax({
		    type:"HEAD",
		    url: imgPath,
		    async: false
		  });
		  return http.status!=404;			
	 }

/*
 * Used to update the wish list
 */
var addToGiftRegistryCallback = function(result, textStatus, jqXHR) {	
	var id = document.getElementById('wishListId').value;
	return redirect("ShowWishListItems.action?wishListId="+id);	
};



		
