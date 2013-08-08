// Set jQuery validation rules
var validationRules = {
    rules: {
        gender: {
            required: true
        },
        firstName: {
            required: true,
            minlength: 2,
            maxlength: 32
        },
        lastName: {
            required: true,
            minlength: 2,
            maxlength: 32
        },
        firstName1: {
            required: true,
            minlength: 2,
            maxlength: 32
        },
        lastName1: {
            required: true,
            minlength: 2,
            maxlength: 32
        },
        birthDateString: {
            required: true
        },
        emailAddr: {
            required: true,
            email: true,
            maxlength: 96
        },
        emailAddrOptional: {
            email: true,
            maxlength: 96
        },
        company: {
            required: false,
            minlength: 2,
            maxlength: 32
        },
        streetAddress: {
            required: true,
            minlength: 2,
            maxlength: 64
        },
        streetAddress1: {
            required: true,
            minlength: 2,
            maxlength: 64
        },
        suburb: {
            required: false,
            minlength: 2,
            maxlength: 32
        },
        postcode: {
            required: true,
            minlength: 2,
            maxlength: 10
        },
        city: {
            required: true,
            minlength: 2,
            maxlength: 32
        },
        state: {
            required: true,
            maxlength: 32
        },
        telephoneNumber: {
            required: true,
            minlength: 3,
            maxlength: 32
        },
        telephoneNumber1: {
            required: false,
            minlength: 3,
            maxlength: 32
        },
        faxNumber: {
            required: false,
            minlength: 3,
            maxlength: 32
        },
        password: {
            required: true,
            minlength: 8,
            maxlength: 40
        },
        currentPassword: {
            required: true,
            minlength: 8,
            maxlength: 40
        },
        passwordConfirmation: {
            required: true,
            minlength: 8,
            maxlength: 40,
            equalTo:"#password"
        },        
        reviewText: {
            required: true,
            maxlength: 10000
        },
        rating: {
            required: true
        },
        linkURL: {
            required: false,
            minlength: 2,
            maxlength: 255
        },
        eventDateString: {
            required: true
        },
        registryName: {
            required: true,
            minlength: 2,
            maxlength: 128
        },
        cvv: {
            required: true,
            digits: true,
            minlength: 3,
            maxlength: 4
        },
        number: {
            required: true,
            creditcard: true
        },        
        owner: {
            required: true,
            minlength: 2,
            maxlength: 80
        },
        expiryMonth: {
            notExpired: true
        },    
        priceFromStr: {
            number: true
        },
        priceToStr: {
        	number: true
        },      
        searchText: {
        	maxlength: 100
        },      
        couponCode: {
       	 maxlength: 40
        },
        giftCertCode: {
       	 maxlength: 40
        },
        rewardPoints: {
       	 maxlength: 40,
       	 digits: true 
       }
    },
	highlight: function(element, errorClass, validClass) {
		var reqElement = $(element).parent().children(".required-icon");
		if (reqElement == null || reqElement.length == 0) {
			reqElement = $(element).parent().parent().children(".required-icon");
		}
		if (reqElement != null ) {
			reqElement.removeClass("required-green").addClass("required-blue");
		}
	  },
	unhighlight: function(element, errorClass, validClass) {
		var reqElement = $(element).parent().children(".required-icon");
		if (reqElement == null || reqElement.length == 0) {
			reqElement = $(element).parent().parent().children(".required-icon");
		}
		if (reqElement != null ) {
			reqElement.removeClass("required-blue").addClass("required-green");
		}
	  },
	errorPlacement: function(error, element) {
		var val = error[0].innerHTML;
		if (val.length > 0) {
			var msgElement = element.parent().children(".validation-msg");
			if (msgElement == null || msgElement.length == 0) {
				msgElement = element.parent().parent().children(".validation-msg");
			}
			if (msgElement != null ) {
				error.appendTo(msgElement);
			}
		}
	}
};


jQuery.validator.addMethod("country", function(countryId, element) {
	return this.optional(element) || countryId > -1;
});	

jQuery.validator.addMethod("state", function(state, element) {
	return this.optional(element) || state != "-1";
});	


function formValidate(form, continueBtn, noSubmit) {
	var val = $('#' + form).validate(validationRules).form();
	if (val) {
		if (noSubmit == undefined || noSubmit == null || noSubmit != 'true') {
			if (continueBtn != undefined && continueBtn != null) {
				$('#' + continueBtn).removeClass().text("").addClass('button-loading');
			}
			document.getElementById(form).submit();
		}
	}
	return val;
}