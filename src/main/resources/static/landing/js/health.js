jQuery(document).ready(function($) {
			$('#btnToggleHelp').on('click', function(event) {
						var $span = $(this).children('span');
						$span.toggleClass("glyphicon-chevron-left");
						$span.toggleClass("glyphicon-chevron-down");
					});

			$.scrollUp({
						scrollName : 'scrollUp',
						topDistance : '300',
						topSpeed : 300,
						animation : 'fade',
						animationInSpeed : 100,
						animationOutSpeed : 200,
						scrollText : '',
						activeOverlay : false
					});
		});