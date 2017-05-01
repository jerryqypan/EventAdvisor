-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema qp7
-- -----------------------------------------------------


-- -----------------------------------------------------
-- Table `qp7`.`Event`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `qp7`.`Event` (
  `idEvent` INT(11) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(45) NOT NULL,
  `date` DATE NOT NULL,
  `startTime` TIME NOT NULL,
  `endTime` TIME NOT NULL,
  `description` TINYTEXT NOT NULL,
  `longitude` DOUBLE NOT NULL,
  `latitude` DOUBLE NOT NULL,
  `place` VARCHAR(45) NOT NULL,
  `uid` VARCHAR(45) NOT NULL,
  `url` VARCHAR(100) NULL DEFAULT '',
  PRIMARY KEY (`idEvent`),
  UNIQUE INDEX `idEvent_UNIQUE` (`idEvent` ASC))
ENGINE = InnoDB
AUTO_INCREMENT = 129
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `qp7`.`EventUsers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `qp7`.`EventUsers` (
  `uid` VARCHAR(45) NOT NULL,
  `name` VARCHAR(45) NULL DEFAULT NULL,
  `email` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`uid`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `qp7`.`EventInterest`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `qp7`.`EventInterest` (
  `idEvent` INT(11) NOT NULL,
  `uid` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`idEvent`, `uid`),
  INDEX `uid_idx` (`uid` ASC),
  CONSTRAINT `idEvent`
    FOREIGN KEY (`idEvent`)
    REFERENCES `qp7`.`Event` (`idEvent`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `uid`
    FOREIGN KEY (`uid`)
    REFERENCES `qp7`.`EventUsers` (`uid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;




SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
