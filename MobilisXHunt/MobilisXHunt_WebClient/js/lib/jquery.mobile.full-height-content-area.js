/* 
 * Some orientation changes leave the scroll position at something
 * that isn't 0,0. This is annoying for user experience.
 * 
 * http://www.semicomplete.com/blog/geekery/jquery-mobile-full-height-content
 */

$(window).on('orientationchange resize pageshow', function() {

	scroll(0, 0);

	/* Calculate the geometry that our content area should take */
	var header  = $('#header:visible');
	var footer  = $('#footer:visible');
	var content = $('#content:visible');
	var viewportHeight = $(window).height();
	var contentHeight = viewportHeight - header.outerHeight();// - footer.outerHeight();

	/* Trim margin/border/padding height */
	contentHeight -= (content.outerHeight() - content.height());
	content.height(contentHeight);
});
