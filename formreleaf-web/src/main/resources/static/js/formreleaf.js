$(function(){

	function headerSizing(){
		var windowHeight = $(window).height()/2; 
		var navHeight = $('#main-nav').height();

		$('.main-header').height((windowHeight - navHeight- 50)); 
	}


	$(window).resize(function(){
		headerSizing();	

	});

	headerSizing();



});