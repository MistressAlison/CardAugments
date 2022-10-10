package CardAugments.cardmods.uncommon;

import CardAugments.CardAugmentsMod;
import CardAugments.cardmods.AbstractAugment;
import basemod.abstracts.AbstractCardModifier;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.colorless.PanicButton;
import com.megacrit.cardcrawl.cards.purple.Halt;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class AbsoluteMod extends AbstractAugment {
    public static final String ID = CardAugmentsMod.makeID("AbsoluteMod");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;

    boolean modMagic;

    @Override
    public void onInitialApplication(AbstractCard card) {
        if (cardCheck(card, c -> doesntDowngradeMagic() && c.baseMagicNumber >= 3)) {
            modMagic = true;
        }
    }

    @Override
    public float modifyBaseDamage(float damage, DamageInfo.DamageType type, AbstractCard card, AbstractMonster target) {
        if (card.baseDamage > 0) {
            return damage * MAJOR_BUFF;
        }
        return damage;
    }

    @Override
    public float modifyBaseBlock(float block, AbstractCard card) {
        if (card.baseBlock > 0) {
            return block * MAJOR_BUFF;
        }
        return block;
    }

    @Override
    public float modifyBaseMagic(float magic, AbstractCard card) {
        if (modMagic) {
            return magic * MAJOR_BUFF;
        }
        return magic;
    }

    @Override
    public boolean validCard(AbstractCard card) {
        return (card.cost > 0 || card.cost == -1) && (card.baseDamage > 0 || card.baseBlock > 0 || cardCheck(card, c -> doesntDowngradeMagic() && c.baseMagicNumber >= 3)) && card.rarity != AbstractCard.CardRarity.BASIC;
    }

    @Override
    public String modifyName(String cardName, AbstractCard card) {
        return TEXT[0] + cardName + TEXT[1];
    }

    @Override
    public String modifyDescription(String rawDescription, AbstractCard card) {
        return rawDescription + TEXT[2];
    }

    @Override
    public boolean betterCanPlay(AbstractCard cardWithThisMod, AbstractCard cardToCheck) {
        if (cardWithThisMod == cardToCheck || hasThisMod(cardToCheck)) {
            return true;
        }
        cardToCheck.cantUseMessage = TEXT[4] + cardWithThisMod.name + TEXT[5];
        return false;
    }

    @Override
    public AugmentRarity getModRarity() {
        return AugmentRarity.UNCOMMON;
    }

    @Override
    public AbstractCardModifier makeCopy() {
        return new AbsoluteMod();
    }

    @Override
    public String identifier(AbstractCard card) {
        return ID;
    }
}