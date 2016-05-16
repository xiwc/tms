jQuery(document).ready(function($) {
	$('#btnToggleHelp').on('click', function(event) {
		var $span = $(this).children('span');
		$span.toggleClass("glyphicon-chevron-left");
		$span.toggleClass("glyphicon-chevron-down");
	});
});