(function(){var P$=Clazz.newPackage("com.falstad"),I$=[];
var C$=Clazz.newClass(P$, "EMWave1Layout", null, null, 'java.awt.LayoutManager');

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
var insets = target.insets();
var targetw = target.size().width - insets.left - insets.right ;
var cw = (targetw * 2/3|0);
var targeth = target.size().height - (insets.top + insets.bottom);
target.getComponent$I(0).move$I$I(insets.left, insets.top);
target.getComponent$I(0).resize$I$I(cw, targeth);
var barwidth = targetw - cw;
cw = cw+(insets.left);
var i;
var h = insets.top;
for (i = 1; i < target.getComponentCount(); i++) {
var m = target.getComponent$I(i);
if (m.isVisible()) {
var d = m.getPreferredSize();
if (Clazz.instanceOf(m, "a2s.Scrollbar")) d.width = barwidth;
if (Clazz.instanceOf(m, "a2s.Choice") && d.width > barwidth ) d.width = barwidth;
if (Clazz.instanceOf(m, "a2s.Label")) {
h = h+((d.height/5|0));
d.width = barwidth;
}m.move$I$I(cw, h);
m.resize$I$I(d.width, d.height);
h = h+(d.height);
}}
});
})();
//Created 2017-12-17 19:28:04
