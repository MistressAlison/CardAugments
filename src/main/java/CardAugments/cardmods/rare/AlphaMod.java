package CardAugments.cardmods.rare;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import CardAugments.cardmods.util.BetaMod;
import CardAugments.cardmods.util.OmegaMod;
import CardAugments.cardmods.util.PreviewedMod;
import CardAugments.patches.InterruptUseCardFieldPatches;
import CardAugments.util.FormatHelper;
import CardAugments.util.PortraitHelper;
import basemod.abstracts.AbstractCardModifier;
import basemod.helpers.CardModifierManager;
import basemod.patches.com.megacrit.cardcrawl.cards.AbstractCard.MultiCardPreview;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.actions.utility.UseCardAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;

public class AlphaMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID(AlphaMod.class.getSimpleName());
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    public static final String[] CARD_TEXT = CardCrawlGame.languagePack.getUIString(ID).EXTRA_TEXT;

    private boolean inherentHack = true;

    @Override
    public void onInitialApplication(AbstractCard card) {
        AbstractCard preview1 = card.makeStatEquivalentCopy();
        AbstractCard preview2 = card.makeStatEquivalentCopy();
        inherentHack = false;
        CardModifierManager.addModifier(preview1, new BetaMod());
        CardModifierManager.addModifier(preview2, new OmegaMod());
        MultiCardPreview.add(card, preview1, preview2);
        InterruptUseCardFieldPatches.InterceptUseField.interceptUse.set(card, true);
        card.cost -= 2;
        card.costForTurn -= 2;
        if (card.cost < 0) {
            card.cost = 0;
        }
        if (card.costForTurn < 0) {
            card.costForTurn = 0;
        }
        card.isEthereal = false;
        card.exhaust = true;
        card.target = AbstractCard.CardTarget.NONE;
        if (card.type != AbstractCard.CardType.SKILL) {
            card.type = AbstractCard.CardType.SKILL;
            PortraitHelper.setMaskedPortrait(card);
        }
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return noShenanigans(card) && cardCheck(card, c -> c.cost >= 2 && doesntUpgradeCost() && (c.baseDamage > 0 || c.baseBlock > 0 || doesntDowngradeMagic()));
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
        return (card.isInnate ? CARD_TEXT[2] : "") + String.format(CARD_TEXT[0], FormatHelper.prefixWords(card.name, "*")) + (card.type == AbstractCard.CardType.POWER ? "" : CARD_TEXT[1]);
    }

    @Override
    public void onUpgradeCheck(AbstractCard card) {
        for (AbstractCard c : MultiCardPreview.multiCardPreview.get(card)) {
            if (CardModifierManager.hasModifier(c, BetaMod.ID) || CardModifierManager.hasModifier(c, OmegaMod.ID)) {
                c.upgrade();
                c.initializeDescription();
            }
        }
        card.initializeDescription();
    }

    @Override
    public void onUse(AbstractCard card, AbstractCreature target, UseCardAction action) {
        AbstractCard preview = null;
        for (AbstractCard c : MultiCardPreview.multiCardPreview.get(card)) {
            if (CardModifierManager.hasModifier(c, BetaMod.ID)) {
                preview = c;
            }
        }
        if (preview != null) {
            AbstractCard copy = preview.makeStatEquivalentCopy();
            this.addToBot(new MakeTempCardInDrawPileAction(copy, 1, true, true));
        }
        /*inherentHack = true;
        AbstractCard copy = card.makeStatEquivalentCopy();
        copy.target = originalTarget;
        inherentHack = false;
        action.exhaustCard = true;
        CardModifierManager.addModifier(copy, new BetaMod());
        this.addToBot(new MakeTempCardInDrawPileAction(copy, 1, true, true));*/
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.RARE;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AlphaMod();
    }

    @Override
    public boolean isInherent(AbstractCard card) {
        return inherentHack;
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }

}
