package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Clash;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class ClashyMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("ClashyMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    @Override
    public void onInitialApplication(AbstractCard card) {
        modifyBaseStat(card, BuffType.DAMAGE, BuffScale.MINOR_BUFF);
        modifyBaseStat(card, AbstractAugment.BuffType.DAMAGE, AbstractAugment.BuffScale.MAJOR_BUFF);
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return card.type == AbstractCard.CardType.ATTACK && card.baseDamage > 0;
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        if (card instanceof Clash) {
            return rawDescription;
        }
        return TEXT[2] + rawDescription;
    }

    @Override
    public boolean canPlayCard(AbstractCard card) {
        for (AbstractCard c : AbstractDungeon.player.hand.group) {
            if (c.type != AbstractCard.CardType.ATTACK) {
                return false;
            }
        }
        return true;
    }

    @Override
    public AbstractAugment.AugmentRarity getModRarity() {
        return AbstractAugment.AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new ClashyMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}