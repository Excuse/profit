package all.util;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class DefaultRecordsModel extends DefaultTableModel {
	@Override
	public Class<? extends Object> getColumnClass(int col) {
		  switch(col) {
		  case 0: return Integer.class;
		  case 1: return Integer.class;
		  case 2: return Integer.class;
		  case 3: return Integer.class;
		  case 4: return String.class;
		  case 5: return Double.class;
		  case 6: return Double.class;
		  default: return Object.class;
		  }
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}
