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
<% com.konakart.al.KKAppEng kkEng = (com.konakart.al.KKAppEng) session.getAttribute("konakartKey");%>

	<div id="slideshow"  class="rounded-corners" >
		<a href="SelectProd.action?prodId=34"><div id="slide-1" class="slide rounded-corners" style="background-image: url('images/banners/home-page/kindle-fire-hd.jpg');"></div></a>  			
	</div>
	<div id="banners">
		<a href="ShowSpecials.action"><div id="banner-1" class="banner-small rounded-corners" style="background-image: url('images/banners/home-page/electronics-sale.jpg');"></div></a>
		<a href="SelectCat.action?catId=24"><div id="banner-2" class="banner-small rounded-corners" style="background-image: url('images/banners/home-page/gifts-for-the-home.jpg');"></div></a>
		<a href="SelectProd.action?prodId=32"><div id="banner-2" class="banner-small rounded-corners" style="background-image: url('images/banners/home-page/windows-8.jpg');"></div></a>
		<a href="SelectProd.action?prodId=35"><div id="banner-2" class="banner-small rounded-corners last-child" style="background-image: url('images/banners/home-page/iphone-5.jpg');"></div></a>
	</div>


