package CardAugments.patches.events;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.rare.FragileMod;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.shrines.AccursedBlacksmith;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import javassist.CtBehavior;

public class BlacksmithPatches {
    public static final String[] MY_TEXT = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("BlacksmithEvent")).TEXT;
    public static int myIndex = -1;
    public static boolean choseMyOption = false;
    static AbstractAugment augment = null;
    @SpirePatch2(clz = AccursedBlacksmith.class, method = SpirePatch.CONSTRUCTOR)
    public static class EventInit {
        @SpirePostfixPatch
        public static void addOption(AccursedBlacksmith __instance) {
            if (CardAugmentsMod.eventAddons) {
                choseMyOption = false;
                augment = new FragileMod();
                //Rip the leave button out and put it back later
                myIndex = __instance.imageEventText.optionList.size() - 1;
                if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(c -> augment.validCard(c))) {
                    __instance.imageEventText.updateDialogOption(myIndex, MY_TEXT[0]);
                } else {
                    __instance.imageEventText.updateDialogOption(myIndex, MY_TEXT[1], true);
                }
                __instance.imageEventText.setDialogOption(MY_TEXT[2]);
            }
        }
    }

    @SpirePatch2(clz = AccursedBlacksmith.class, method = "buttonEffect")
    public static class ButtonLogic {
        @SpirePrefixPatch
        public static SpireReturn<?> buttonPress(AccursedBlacksmith __instance, @ByRef int[] buttonPressed, @ByRef int[] ___screenNum) {
            if (CardAugmentsMod.eventAddons) {
                if (___screenNum[0] == 0) {
                    //If we click the new leave button, let it act as if we pressed the old leave button
                    if (buttonPressed[0] == myIndex + 1) {
                        buttonPressed[0] = 2;
                        return SpireReturn.Continue();
                    }
                    if (buttonPressed[0] == myIndex) {
                        __instance.imageEventText.clearRemainingOptions();
                        __instance.imageEventText.updateBodyText(MY_TEXT[3]);
                        __instance.imageEventText.updateDialogOption(0, MY_TEXT[2]);
                        CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
                        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
                            if (augment.validCard(c)) {
                                group.addToBottom(c);
                            }
                        }
                        AbstractDungeon.gridSelectScreen.open(group, 1, MY_TEXT[4], false, false, false, false);
                        choseMyOption = true;
                        ___screenNum[0] = 2;
                        return SpireReturn.Return();
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AccursedBlacksmith.class, method = "update")
    public static class UpdateSnag {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<?> update(AccursedBlacksmith __instance) {
            if (CardAugmentsMod.eventAddons) {
                if (choseMyOption) {
                    if (!AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                        AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                        CardModifierManager.addModifier(c, augment);
                        AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                        AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                        AbstractDungeon.gridSelectScreen.selectedCards.clear();
                        AbstractEvent.logMetricCardUpgrade("Accursed Blacksmith", "Tinker", c);
                        choseMyOption = false;
                    }
                    return SpireReturn.Return();
                }
            }
            return SpireReturn.Continue();
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.FieldAccessMatcher(AccursedBlacksmith.class, "pickCard");
                return LineFinder.findInOrder(ctBehavior, m);
            }
        }
    }
}