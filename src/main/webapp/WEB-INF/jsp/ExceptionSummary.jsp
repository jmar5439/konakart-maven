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

<script>
$(function() {
	$("#exception-more").click(function() {
		$("#exception-message").width(300);
		$("#exception-details").show();
		$("#exception-more").hide();
		$("#exception-less").show();
	});
	
	$("#exception-less").click(function() {
		$("#exception-message").width(900);
		$("#exception-details").hide();
		$("#exception-less").hide();
		$("#exception-more").show();
	});
});
</script>

<% com.konakart.al.KKAppEng kkEng = (com.konakart.al.KKAppEng) session.getAttribute("konakartKey");  %>

 				<h1 id="page-title"><kk:msg  key="exception.title"/></h1>			
	    		<div class="content-area rounded-corners">
		    		<div id="exception">
						<div class="exception-container">
							<div id="exception-message">
								<kk:msg  key="exception.short.message"/>
							</div>
							<s:if test="hasActionErrors()">							
								<div id="exception-details">									
							        <s:iterator value="actionErrors">  
							            <s:property escape="false"/>
							        </s:iterator>  
								</div>
								<div class="exception-more-button">
									<a id="exception-more" class="button-medium small-rounded-corners">
										<span><kk:msg  key="common.more"/></span>
									</a>
									<a id="exception-less" class="button-medium small-rounded-corners">
										<span><kk:msg  key="common.less"/></span>
									</a>
								</div>
							</s:if>	
						</div>
			    	</div>
	    		</div>


