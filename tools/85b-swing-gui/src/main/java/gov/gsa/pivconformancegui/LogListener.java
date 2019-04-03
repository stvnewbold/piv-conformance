package gov.gsa.pivconformancegui;

import javax.swing.text.BoxView;
import javax.swing.text.Element;

public class LogListener extends BoxView {

	public LogListener(Element elem, int axis) {
		super(elem, axis);
	}
	
	void doAddEvent(LogEvent l) {
		StructuredEvent se = new StructuredEvent(l);
		Utils.AppendToBoxView(se, this);
	}

}
