//package CardAugments.patches;
//
//import CardAugments.cardmods.AbstractAugment;
//import basemod.abstracts.AbstractCardModifier;
//import basemod.helpers.CardModifierManager;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.g2d.GlyphLayout;
//import com.evacipated.cardcrawl.modthespire.lib.SpireField;
//import com.evacipated.cardcrawl.modthespire.lib.SpireInstrumentPatch;
//import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
//import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
//import com.megacrit.cardcrawl.cards.AbstractCard;
//import com.megacrit.cardcrawl.core.Settings;
//import com.megacrit.cardcrawl.helpers.FontHelper;
//import com.megacrit.cardcrawl.screens.SingleCardViewPopup;
//import javassist.CannotCompileException;
//import javassist.expr.ExprEditor;
//import javassist.expr.MethodCall;
//
//public class ChangeRenderedNamePatches {
//    private static final float IMG_WIDTH = 300.0F * Settings.scale;
//    private static final float TITLE_BOX_WIDTH = IMG_WIDTH * 0.7F;
//    private static final float TITLE_BOX_WIDTH_NO_COST = IMG_WIDTH * 0.8F;
//    private static final GlyphLayout gl = new GlyphLayout();
//    private static String builtString;
//
//    @SpirePatch(clz = AbstractCard.class, method = SpirePatch.CLASS)
//    public static class PrefixSuffixFields {
//        public static SpireField<String> prefix = new SpireField<>(() -> "");
//        public static SpireField<String> suffix = new SpireField<>(() -> "");
//    }
//
//    public static String buildName(AbstractCard card, boolean isSCV) {
//        String upgradeNotation;
//        String renderName = card.name;
//        for (AbstractCardModifier m : CardModifierManager.modifiers(card))
//            if (m instanceof AbstractAugment) {
//
//            }
//        if (PrefixSuffixFields.prefix.get(card).isEmpty() && PrefixSuffixFields.suffix.get(card).isEmpty()) {
//            return card.name;
//        } else {
//            builtString = PrefixSuffixFields.prefix.get(card) + card.name + PrefixSuffixFields.suffix.get(card);
//            if (isSCV) {
//                FontHelper.SCP_cardTitleFont_small.getData().setScale(1.0F);
//                gl.setText(FontHelper.SCP_cardTitleFont_small, builtString, Color.WHITE, 0.0F, 1, false);
//                float scale = Math.min(1, (card.cost == -2 ? TITLE_BOX_WIDTH_NO_COST * 2: TITLE_BOX_WIDTH * 2) / gl.width);
//                FontHelper.SCP_cardTitleFont_small.getData().setScale(scale);
//            } else {
//                FontHelper.cardTitleFont.getData().setScale(1.0F);
//                gl.setText(FontHelper.cardTitleFont, builtString, Color.WHITE, 0.0F, 1, false);
//                float scale = Math.min(1, (card.cost == -2 ? TITLE_BOX_WIDTH_NO_COST : TITLE_BOX_WIDTH) / gl.width);
//                FontHelper.cardTitleFont.getData().setScale(card.drawScale * scale);
//            }
//            gl.reset();
//            return builtString;
//        }
//    }
//
//    @SpirePatch2(clz = AbstractCard.class, method = "renderTitle")
//    public static class BeDifferentNamePls {
//        @SpireInstrumentPatch
//        public static ExprEditor patch() {
//            return new ExprEditor() {
//                @Override
//                //Method call is basically the equivalent of a methodcallmatcher of an insert patch, checks the edit method against every method call in the function you#re patching
//                public void edit(MethodCall m) throws CannotCompileException {
//                    //If the method is from the class AnimationState and the method is called update
//                    if (m.getClassName().equals(FontHelper.class.getName()) && m.getMethodName().equals("renderRotatedText")) {
//                        m.replace("{" +
//                                //"if(M10Robot.patches.BoosterFieldPatch.hasBoosterEquipped(this)) {" +
//                                //$1 refers to the first input parameter of the method, in this case the float that Gdx.graphics.getDeltaTime() returns
//                                "$3 = CardAugments.patches.ChangeRenderedNamePatches.buildName(this, false);" +
//                                //"$10 = M10Robot.patches.ColorRenderingPatches.getCardNameColor(this, $10);" +
//                                //"}" +
//                                //Call the method as normal
//                                "$proceed($$);" +
//                                "}");
//                    }
//                }
//            };
//        }
//    }
//
//    @SpirePatch2(clz = SingleCardViewPopup.class, method = "renderTitle")
//    public static class BeDifferentNamePls2 {
//        @SpireInstrumentPatch
//        public static ExprEditor patch() {
//            return new ExprEditor() {
//                @Override
//                //Method call is basically the equivalent of a methodcallmatcher of an insert patch, checks the edit method against every method call in the function you#re patching
//                public void edit(MethodCall m) throws CannotCompileException {
//                    //If the method is from the class AnimationState and the method is called update
//                    if (m.getClassName().equals(FontHelper.class.getName()) && m.getMethodName().equals("renderFontCentered")) {
//                        m.replace("{" +
//                                //"if(M10Robot.patches.BoosterFieldPatch.hasBoosterEquipped(this)) {" +
//                                //$1 refers to the first input parameter of the method, in this case the float that Gdx.graphics.getDeltaTime() returns
//                                "$3 = CardAugments.patches.ChangeRenderedNamePatches.buildName(this.card, true);" +
//                                //"$10 = M10Robot.patches.ColorRenderingPatches.getCardNameColor(this, $10);" +
//                                //"}" +
//                                //Call the method as normal
//                                "$proceed($$);" +
//                                "}");
//                    }
//                }
//            };
//        }
//    }
//}
