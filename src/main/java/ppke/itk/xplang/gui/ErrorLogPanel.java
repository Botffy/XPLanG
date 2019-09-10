package ppke.itk.xplang.gui;

import ppke.itk.xplang.common.ErrorLog;

import javax.swing.*;
import java.awt.*;

class ErrorLogPanel implements CompilerResultListener {
    private final JPanel panel;
    private final ErrorLogTableModel data;

    ErrorLogPanel() {
        this.panel = new JPanel(new BorderLayout());
        this.data = new ErrorLogTableModel(new ErrorLog());
        JTable table = new JTable(this.data);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        this.panel.add(scrollPane);
    }

    JPanel getPanel() {
        return panel;
    }

    @Override
    public void onCompilerResult(Compiler.Result result) {
        this.data.setErrorLog(result.getErrorLog());
    }
}
