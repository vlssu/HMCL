/*
 * Copyright (C) 2015 Jack Jiang(cngeeker.com) The BeautyEye Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/beautyeye
 * Version 3.6
 * 
 * Jack Jiang PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * BEButtonUI.java at 2015-2-1 20:25:36, original version by Jack Jiang.
 * You can contact author with jb2011@163.com.
 */
package org.jackhuang.hmcl.laf.button;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.JTextComponent;
import org.jackhuang.hmcl.laf.BEUtils;

import org.jackhuang.hmcl.laf.utils.Icon9Factory;

/**
 * JButton的UI实现类.
 *
 * @author Jack Jiang(jb2011@163.com)
 * @version 1.0
 * @see com.sun.java.swing.plaf.windows.WindowsButtonUI
 */
public class BEButtonUI extends BasicButtonUI {

    private static final Icon9Factory ICON_9 = new Icon9Factory("button");

    private final static BEButtonUI INSTANCE = new BEButtonUI();

    /**
     * The dashed rect gap x.
     */
    protected int dashedRectGapX;

    /**
     * The dashed rect gap y.
     */
    protected int dashedRectGapY;

    /**
     * The dashed rect gap width.
     */
    protected int dashedRectGapWidth;

    /**
     * The dashed rect gap height.
     */
    protected int dashedRectGapHeight;

    /**
     * The focus color.
     */
    protected Color focusColor;

    /**
     * The defaults_initialized.
     */
    private boolean defaults_initialized = false;

    // ********************************
    //          Create PLAF
    // ********************************
    public static ComponentUI createUI(JComponent c) {
        if (c instanceof CustomButton)
            return new CustomButtonUI();
        return INSTANCE;
    }

    // ********************************
    //            Defaults
    // ********************************
    @Override
    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        LookAndFeel.installProperty(b, "opaque", Boolean.FALSE);

        if (!defaults_initialized) {
            String pp = getPropertyPrefix();
            dashedRectGapX = UIManager.getInt(pp + "dashedRectGapX");
            dashedRectGapY = UIManager.getInt(pp + "dashedRectGapY");
            dashedRectGapWidth = UIManager.getInt(pp + "dashedRectGapWidth");
            dashedRectGapHeight = UIManager.getInt(pp + "dashedRectGapHeight");
            focusColor = UIManager.getColor(pp + "focus");
            defaults_initialized = true;
        }

        {
            LookAndFeel.installProperty(b, "rolloverEnabled", Boolean.TRUE);
        }
    }

    @Override
    protected void uninstallDefaults(AbstractButton b) {
        super.uninstallDefaults(b);
        defaults_initialized = false;
    }

    /**
     * Gets the focus color.
     *
     * @return the focus color
     */
    protected Color getFocusColor() {
        return focusColor;
    }

    // ********************************
    //         Paint Methods
    // ********************************
    @Override
    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        // focus painted same color as text on Basic??
        int width = b.getWidth();
        int height = b.getHeight();
        g.setColor(getFocusColor());

        //** modified by jb2011：绘制虚线方法改成可以设置虚线步进的方法，步进设为2则更好看一点
//		BasicGraphicsUtils.drawDashedRect(g, dashedRectGapX, dashedRectGapY,
//				width - dashedRectGapWidth, height - dashedRectGapHeight);
        // 绘制虚线框
        BEUtils.drawDashedRect(g, dashedRectGapX, dashedRectGapY,
                width - dashedRectGapWidth, height - dashedRectGapHeight);
        // 绘制虚线框的半透明白色立体阴影（半透明的用处在于若隐若现的效果比纯白要来的柔和的多）
        g.setColor(new Color(255, 255, 255, 50));
        // 立体阴影就是向右下偏移一个像素实现的
        BEUtils.drawDashedRect(g, dashedRectGapX + 1, dashedRectGapY + 1,
                width - dashedRectGapWidth, height - dashedRectGapHeight);
    }

    // ********************************
    //          Layout Methods
    // ********************************
    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension d = super.getPreferredSize(c);

        /* Ensure that the width and height of the button is odd,
		 * to allow for the focus line if focus is painted
         */
        AbstractButton b = (AbstractButton) c;
        if (d != null && b.isFocusPainted()) {
            if (d.width % 2 == 0)
                d.width += 1;
            if (d.height % 2 == 0)
                d.height += 1;
        }
        return d;
    }


    /* These rectangles/insets are allocated once for all 
	 * ButtonUI.paint() calls.  Re-using rectangles rather than 
	 * allocating them in each paint call substantially reduced the time
	 * it took paint to run.  Obviously, this method can't be re-entered.
     */
//	private static Rectangle viewRect = new Rectangle();
    @Override
    public void paint(Graphics g, JComponent c) {
        paintXPButtonBackground(g, c);
        super.paint(g, c);
    }

    /**
     * Paint xp button background.
     *
     * @param nomalColor the nomal color
     * @param g the g
     * @param c the c
     */
    public static void paintXPButtonBackground(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        boolean toolbar = b.getParent() instanceof JToolBar;

        if (b.isContentAreaFilled()) {
            ButtonModel model = b.getModel();
            Dimension d = c.getSize();
            int dx = 0;
            int dy = 0;
            int dw = d.width;
            int dh = d.height;

            Border border = c.getBorder();
            Insets insets;
            if (border != null)
                // Note: The border may be compound, containing an outer
                // opaque border (supplied by the application), plus an
                // inner transparent margin border. We want to size the
                // background to fill the transparent part, but stay
                // inside the opaque part.
                insets = BEButtonUI.getOpaqueInsets(border, c);
            else
                insets = c.getInsets();
            if (insets != null) {
                dx += insets.left;
                dy += insets.top;
                dw -= (insets.left + insets.right);
                dh -= (insets.top + insets.bottom);
            }

            if (toolbar)
                //此状态下JToggleButton和JButton使用各自的背景实现，2012-10-16前无论是不是JToggleButton都是使用该种实是不太合理的
                if (model.isRollover() || model.isPressed())
                    if (c instanceof JToggleButton)
                        BEToggleButtonUI.ICON_9.get("rollover").draw((Graphics2D) g, dx, dy, dw, dh);
                    else
                        ICON_9.get("pressed").draw((Graphics2D) g, dx, dy, dw, dh);
                else if (model.isSelected())
                    BEToggleButtonUI.ICON_9.get("selected").draw((Graphics2D) g, dx, dy, dw, dh);
                else {
                    //TODO 其它状态下的按钮背景样式需要完善，要不然看起来太硬！
//					skin.paintSkin(g, dx, dy, dw, dh, state);
                }
            else {
                //TODO 其它状态下的按钮背景样式需要完善，要不然看起来太硬！
                String key;
                if (model.isArmed() && model.isPressed() || model.isSelected())
                    key = "pressed";
                else if (!model.isEnabled())
                    key = "disabled";
                else if (model.isRollover())
                    key = "rollover";
                else
                    key = "normal";
                ICON_9.get(key).draw((Graphics2D) g, dx, dy, dw, dh);
            }
        }
    }

    /**
     * returns - b.getBorderInsets(c) if border is opaque - null if border is
     * completely non-opaque - somewhere inbetween if border is compound and
     * outside border is opaque and inside isn't
     *
     * @param b the b
     * @param c the c
     * @return the opaque insets
     */
    private static Insets getOpaqueInsets(Border b, Component c) {
        if (b == null)
            return null;
        if (b.isBorderOpaque())
            return b.getBorderInsets(c);
        else if (b instanceof CompoundBorder) {
            CompoundBorder cb = (CompoundBorder) b;
            Insets iOut = getOpaqueInsets(cb.getOutsideBorder(), c);
            if (iOut != null && iOut.equals(cb.getOutsideBorder().getBorderInsets(c))) {
                // Outside border is opaque, keep looking
                Insets iIn = getOpaqueInsets(cb.getInsideBorder(), c);
                if (iIn == null)
                    // Inside is non-opaque, use outside insets
                    return iOut;
                else
                    // Found non-opaque somewhere in the inside (which is
                    // also compound).
                    return new Insets(iOut.top + iIn.top, iOut.left + iIn.left,
                            iOut.bottom + iIn.bottom, iOut.right + iIn.right);
            } else
                // Outside is either all non-opaque or has non-opaque
                // border inside another compound border
                return iOut;
        } else
            return null;
    }

    public static class BEEmptyBorder extends EmptyBorder implements UIResource {

        public BEEmptyBorder(Insets m) {
            super(m.top + 2, m.left + 2, m.bottom + 2, m.right + 2);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, getBorderInsets());
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets = super.getBorderInsets(c, insets);

            Insets margin = null;
            if (c instanceof AbstractButton) {
                Insets m = ((AbstractButton) c).getMargin();
                // if this is a toolbar button then ignore getMargin()
                // and subtract the padding added by the constructor
                if (c.getParent() instanceof JToolBar
                        && !(c instanceof JRadioButton)
                        && !(c instanceof JCheckBox)
                        && m instanceof InsetsUIResource) {
                    insets.top -= 2;
                    insets.left -= 2;
                    insets.bottom -= 2;
                    insets.right -= 2;
                } else
                    margin = m;
            } else if (c instanceof JToolBar)
                margin = ((JToolBar) c).getMargin();
            else if (c instanceof JTextComponent)
                margin = ((JTextComponent) c).getMargin();
            if (margin != null) {
                insets.top = margin.top + 2;
                insets.left = margin.left + 2;
                insets.bottom = margin.bottom + 2;
                insets.right = margin.right + 2;
            }
            return insets;
        }
    }
}
