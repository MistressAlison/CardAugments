package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.patches.InfiniteUpgradesPatches;
import basemod.abstracts.AbstractCardModifier;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.BranchingUpgradesCard;
import com.evacipated.cardcrawl.mod.stslib.cards.interfaces.MultiUpgradeCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class SearingMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(SearingMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        InfiniteUpgradesPatches.InfUpgradeField.inf.set(card, true);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.type == AbstractCard.CardType.ATTACK || card.type == AbstractCard.CardType.SKILL) && card.canUpgrade() && cardCheck(card, c -> upgradesAVariable()) && doesntOverride(card, "canUpgrade") && !(card instanceof BranchingUpgradesCard) && !(card instanceof MultiUpgradeCard);
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
        return insertAfterText(rawDescription , CARD_TEXT[0]);
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new SearingMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}
