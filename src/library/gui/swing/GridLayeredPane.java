package library.gui.swing;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class GridLayeredPane extends JPanel {

    private final int SizeX;
    private final int SizeY;

    // ★追加: 配置情報を保持する内部クラス
    private static class LayoutInfo {
        Component component;
        int x, y, w, h; // グリッド座標とサイズ

        public LayoutInfo(Component component, int x, int y, int w, int h) {
            this.component = component;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }

    // ★追加: 登録されたコンポーネントの配置情報リスト
    private final List<LayoutInfo> layoutList = new ArrayList<>();

    // sizeX,Yは分割の数
    public GridLayeredPane(int sizeX, int sizeY) {
        SizeX = sizeX;
        SizeY = sizeY;
        setLayout(null); // pa = this; は不要、thisでアクセス可能

        // ★修正: パネルのサイズ変更イベントを捕捉するリスナーを追加
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // サイズが変更されたとき、またはサイズが確定したときに再配置を実行
                repositionAllComponents();
            }
        });
    }

    /**
     * ★追加: すべてのコンポーネントを現在のパネルサイズに基づいて再配置する
     */
    private void repositionAllComponents() {
        int w2 = getWidth();
        int h2 = getHeight();

        // サイズが0の場合、まだ配置が不可能なので処理をスキップ
        if (w2 <= 0 || h2 <= 0) {
            return;
        }

        for (LayoutInfo info : layoutList) {
            // グリッド座標をピクセル座標に変換
            int logicalX = info.x * w2 / SizeX;
            int logicalY = info.y * h2 / SizeY;
            int logicalW = info.w * w2 / SizeX;
            int logicalH = info.h * h2 / SizeY;

            info.component.setBounds(logicalX, logicalY, logicalW, logicalH);
        }
        // 親コンポーネントに再描画を要求 (親のJFrame/JDialogのrepaint()も実行されるべき)
        repaint();
    }

    @Override
    public void doLayout() {
        // doLayoutが呼ばれた際も再配置を実行することで、
        // frame.setVisible(true) や frame.pack() 後のサイズ確定時に対応する
        repositionAllComponents();
        super.doLayout();
    }


    public void add(Component p, int x, int y, int w, int h) {
        // ★修正: コンポーネントをリストに登録し、親のaddを呼ぶ
        layoutList.add(new LayoutInfo(p, x, y, w, h));
        super.add(p);

        // コンポーネント追加後、既にサイズを持っている場合は即時配置を試みる
        if (getWidth() > 0 && getHeight() > 0) {
            repositionAllComponents();
        }
    }
}