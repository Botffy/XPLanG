package ppke.itk.xplang.gui;

import ppke.itk.xplang.common.ErrorLog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

class ErrorLogPanel implements CompilerResultListener {
    private final JPanel panel;
    private final ErrorLogTableModel data;

    ErrorLogPanel() {
        this.panel = new JPanel(new BorderLayout());
        this.data = new ErrorLogTableModel(new ErrorLog());
        JTable table = new JTable(this.data);
        JScrollPane scrollPane = new JScrollPane(table);

        table.setDragEnabled(false);
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);

        table.setShowGrid(false);
        table.setDefaultRenderer(Object.class, new ErrorTableRenderer());
        DefaultTableCellRenderer rightAlignRenderer = new ErrorTableRenderer();
        rightAlignRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(0).setCellRenderer(rightAlignRenderer);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(1).setMaxWidth(40);
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

    private static class ErrorTableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(noFocusBorder);
            return this;
        }
    }
}
