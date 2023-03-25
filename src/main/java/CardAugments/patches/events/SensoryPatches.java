package CardAugments.patches.events;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.event.CultistMod;
import CardAugments.cardmods.event.FanaticMod;
import CardAugments.cardmods.uncommon.AutoMod;
import basemod.ReflectionHacks;
import basemod.helpers.CardModifierManager;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.beyond.SensoryStone;
import com.megacrit.cardcrawl.events.exordium.Sssserpent;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import com.megacrit.cardcrawl.vfx.combat.FlashAtkImgEffect;
import javassist.*;
import javassist.bytecode.DuplicateMemberException;

import java.util.Collections;

public class SensoryPatches {
    public static final String[] MY_TEXT = CardCrawlGame.languagePack.getUIString(CardAugmentsMod.makeID("SensoryEvent")).TEXT;
    public static final int DAMAGE = 5;
    public static int myIndex = -1;
    public static boolean choseMyOption = false;
    public static boolean pickedCard = false;
    public static AbstractAugment augment = null;
    public static Object[] enumElements = null;
    @SpirePatch2(clz = SensoryStone.class, method = SpirePatch.CONSTRUCTOR)
    public static class EventInit {
        @SpirePostfixPatch
        public static void addOption(SensoryStone __instance) {
            if (CardAugmentsMod.eventAddons) {
                choseMyOption = false;
                pickedCard = false;
                augment = null;
                if (enumElements == null) {
                    try {
                        Class<?> enumElement = Class.forName(SensoryStone.class.getName()+"$CurScreen");
                        if (enumElement.isEnum()) {
                            enumElements = enumElement.getEnumConstants();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SpirePatch2(clz = SensoryStone.class, method = "buttonEffect")
    public static class AddButton {
        @SpireInsertPatch(locator = Locator.class)
        public static void add(SensoryStone __instance) {
            myIndex = __instance.imageEventText.optionList.size();
            augment = new AutoMod();
            if (AbstractDungeon.player.masterDeck.group.stream().anyMatch(augment::validCard)) {
                __instance.imageEventText.setDialogOption(String.format(MY_TEXT[0], DAMAGE));
            } else {
                __instance.imageEventText.setDialogOption(MY_TEXT[1], true);
            }
        }

        public static class Locator extends SpireInsertLocator {
            @Override
            public int[] Locate(CtBehavior ctBehavior) throws Exception {
                Matcher m = new Matcher.FieldAccessMatcher(SensoryStone.class, "screen");
                int[] hits = LineFinder.findAllInOrder(ctBehavior, m);
                return new int[]{hits[1]};
            }
        }
    }

    @SpirePatch2(clz = SensoryStone.class, method = "buttonEffect")
    public static class ButtonLogic {
        @SpirePrefixPatch
        public static SpireReturn<?> buttonPress(SensoryStone __instance, int buttonPressed, Object ___screen) {
            if (CardAugmentsMod.eventAddons) {
                if (___screen == enumElements[1]) {
                    if (buttonPressed == myIndex) {
                        pickedCard = true;
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
                        ReflectionHacks.setPrivate(__instance, SensoryStone.class, "screen", enumElements[3]);
                        return SpireReturn.Return();
                    } else {
                        __instance.imageEventText.removeDialogOption(myIndex);
                    }
                }
            }
            return SpireReturn.Continue();
        }
    }

    public static void updateEvent(SensoryStone __instance) {
        if (CardAugmentsMod.eventAddons) {
            if (pickedCard && !AbstractDungeon.isScreenUp && !AbstractDungeon.gridSelectScreen.selectedCards.isEmpty()) {
                AbstractCard c = AbstractDungeon.gridSelectScreen.selectedCards.get(0);
                CardModifierManager.addModifier(c, augment);
                AbstractDungeon.player.damage(new DamageInfo(AbstractDungeon.player, DAMAGE));
                AbstractDungeon.effectList.add(new FlashAtkImgEffect(AbstractDungeon.player.hb.cX, AbstractDungeon.player.hb.cY, AbstractGameAction.AttackEffect.FIRE));
                AbstractDungeon.effectsQueue.add(new ShowCardBrieflyEffect(c.makeStatEquivalentCopy()));
                AbstractDungeon.topLevelEffects.add(new UpgradeShineEffect((float) Settings.WIDTH / 2.0F, (float)Settings.HEIGHT / 2.0F));
                AbstractDungeon.gridSelectScreen.selectedCards.clear();
                pickedCard = false;
                AbstractEvent.logMetric("SensoryStone", "Reject", null, null, null, Collections.singletonList(c.cardID), null, null, null, DAMAGE, 0, 0, 0, 0, 0);
            }
        }
    }

    @SpirePatch2(clz = SensoryStone.class, method = SpirePatch.CONSTRUCTOR)
    public static class AddUpdateOverride {
        @SpireRawPatch
        public static void addMethod(CtBehavior ctMethodToPatch) throws CannotCompileException, NotFoundException {
            CtClass ctClass = ctMethodToPatch.getDeclaringClass();
            CtClass superClass = ctClass.getSuperclass();
            CtMethod superMethod = superClass.getDeclaredMethod("update");
            CtMethod updateMethod = CtNewMethod.delegator(superMethod, ctClass);
            try {
                ctClass.addMethod(updateMethod);
            } catch (DuplicateMemberException ignored) {
                updateMethod = ctClass.getDeclaredMethod("update");
            }
            updateMethod.insertAfter(SensoryPatches.class.getName()+".updateEvent($0);");
        }
    }
}
