package CardAugments.cardmods.event;

import CardAugments.CardAugmentsMod;
import CardAugments.actions.AutoplayOnRandomEnemyAction;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.fields.cards.AbstractCard.AutoplayField;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class AutoMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(AutoMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    public static final int DRAW = 1;

    @Override
    public void onDrawn(AbstractCard card) {
        addToBot(new AutoplayOnRandomEnemyAction(card));
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        addToBot(new DrawCardAction(DRAW));
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return !AutoplayField.autoplay.get(card) && card.cost >= 0 && cardCheck(card, AbstractAugment::notRetain);
    }

    @Override
    public String getPrefix() {
        return TEXT[0];
    }

    @Override
    public String getSuffix() {
        return TEXT[1];
    }

    @Override
    public String getAugmentDescription() {
        return TEXT[2];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return CARD_TEXT[0] + insertAfterText(rawDescription , String.format(CARD_TEXT[1], DRAW));
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.SPECIAL;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AutoMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
