(function(){var P$=Clazz.newPackage("com.falstad"),I$=[];
var C$=Clazz.newClass(P$, "WaveBoxLayout", null, null, 'java.awt.LayoutManager');

C$.$clinit$ = function() {Clazz.load(C$, 1);
}

Clazz.newMeth(C$, '$init$', function () {
}, 1);

Clazz.newMeth(C$, 'c$', function () {
C$.$init$.apply(this);
}, 1);

Clazz.newMeth(C$, 'addLayoutComponent$S$java_awt_Component', function (name, c) {
});

Clazz.newMeth(C$, 'removeLayoutComponent$java_awt_Component', function (c) {
});

Clazz.newMeth(C$, 'preferredLayoutSize$java_awt_Container', function (target) {
return Clazz.new_((I$[0]||(I$[0]=Clazz.load('java.awt.Dimension'))).c$$I$I,[500, 500]);
});

Clazz.newMeth(C$, 'minimumLayoutSize$java_awt_Container', function (target) {
return Clazz.new_((I$[0]||(I$[0]=Clazz.load('java.awt.Dimension'))).c$$I$I,[100, 100]);
});

Clazz.newMeth(C$, 'layoutContainer$java_awt_Container', function (target) {
var barwidth = 0;
var i;
for (i = 1; i < target.getComponentCount(); i++) {
var m = target.getComponent$I(i);
if (m.isVisible()) {
var d = m.getPreferredSize();
if (d.width > barwidth) barwidth = d.width;
}}
var insets = target.getInsets();
var targetw = target.getSize().width - insets.left - insets.right ;
var cw = targetw - barwidth;
var targeth = target.getSize().height - (insets.top + insets.bottom);
target.getComponent$I(0).setLocation$I$I(insets.left, insets.top);
target.getComponent$I(0).setSize$I$I(cw, targeth);
cw = cw+(insets.left);
var h = insets.top;
for (i = 1; i < target.getComponentCount(); i++) {
var m = target.getComponent$I(i);
if (m.isVisible()) {
var d = m.getPreferredSize();
if (d.width >= 0 && d.height >= 0 ) {
if (Clazz.instanceOf(m, "a2s.Scrollbar")) d.width = barwidth;
if (Clazz.instanceOf(m, "a2s.Label")) {
h = h+((d.height/5|0));
d.width = barwidth;
}m.setLocation$I$I(cw, h);
m.setSize$I$I(d.width, d.height);
h = h+(d.height);
}}}
});
})();
//Created 2017-12-17 19:28:17
