package ppke.itk.xplang.gui;

import ppke.itk.xplang.common.CompilerMessage;
import ppke.itk.xplang.common.ErrorLog;

import javax.swing.table.AbstractTableModel;

class ErrorLogTableModel extends AbstractTableModel {
    private ErrorLog errorLog;

    ErrorLogTableModel(ErrorLog errorLog) {
        this.errorLog = errorLog;
    }

    void setErrorLog(ErrorLog errorLog) {
        this.errorLog = errorLog;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return errorLog.getErrorMessages().size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Hely";
        } else if (column == 1) {
            return "Hibaüzenet";
        }

        throw new IllegalStateException();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CompilerMessage compilerMessage = errorLog.getErrorMessages().get(rowIndex);

        if (columnIndex == 0) {
            return compilerMessage.getCursorPosition();
        } else if (columnIndex == 1) {
            return compilerMessage.getMessage();
        }

        throw new IllegalStateException();
    }
}
