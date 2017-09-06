package swingjs.plaf;


import java.awt.Dimension;

import javax.swing.JRootPane;
import javax.swing.LookAndFeel;
import swingjs.api.js.DOMNode;

public class JSPanelUI extends JSLightweightUI {

	int frameZ = 10000;
	public JSPanelUI() {
		isContainer = true;
		setDoc();
	}
	
	@Override
	protected DOMNode updateDOMNode() {
		if (domNode == null) {
			JRootPane root = jc.getRootPane();
			isContentPane = (root != null && jc == root.getContentPane());
			domNode = newDOMObject("div", id);
			if (root != null && root.getGlassPane() == c)
				DOMNode.setStyles(domNode, "display", "none");
		}
    return domNode;
	}

	@Override
  protected Dimension setHTMLSize(DOMNode obj, boolean addCSS) {
		// SwingJS for now: just designated container width/height 
		return new Dimension(c.getWidth(), c.getHeight());
	}
	

	@Override
	protected void installUIImpl() {
    LookAndFeel.installColorsAndFont(jc,
        "Panel.background",
        "Panel.foreground",
        "Panel.font");
	}

	@Override
	protected void uninstallUIImpl() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dimension getPreferredSize() {
  	return null;
  }


}