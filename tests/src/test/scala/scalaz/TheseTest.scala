package scalaz

import scalaz.scalacheck.ScalazProperties._
import scalaz.scalacheck.ScalazArbitrary._
import std.AllInstances._
import \&/._
import syntax.contravariant._
import org.scalacheck.Prop.forAll

object TheseTest extends SpecLite {
  type TheseInt[a] = Int \&/ a

  checkAll(monad.laws[TheseInt])
  checkAll(cobind.laws[TheseInt])
  checkAll(traverse.laws[TheseInt])
  checkAll(order.laws[Int \&/ Int])
  checkAll(semigroup.laws[Int \&/ Int])
  checkAll(bitraverse.laws[\&/])
  checkAll(band.laws[ISet[Int] \&/ ISet[Int]])

  implicit def ephemeralStreamShow[A: Show]: Show[EphemeralStream[A]] =
    Show[List[A]].contramap(_.toList)

  "align unalign" should {
    "List" ! forAll { (a: List[Int], b: List[Int]) =>
      unalignList(alignList(a, b)) must_=== ((a, b))
    }
    "EphemeralStream" ! forAll { (a: EphemeralStream[Int], b: EphemeralStream[Int]) =>
      unalignStream(alignStream(a, b)) must_=== ((a, b))
    }
  }

  "onlyThisOrThat" should {
    "be invertible" ! forAll { ab: Int \/ String =>
      ab.toThese.onlyThisOrThat must_=== Some(ab)
    }
    "handle both" ! forAll { (a: Int, b: String) =>
      \&/.Both(a,b).onlyThisOrThat must_=== None
    }
  }

  object instances {
    def equal[A: Equal, B: Equal] = Equal[A \&/ B]
    def order[A: Order, B: Order] = Order[A \&/ B]
    def functor[L] = Functor[({type λ[α] = L \&/ α})#λ]
    def apply[L: Semigroup] = Apply[({type λ[α] = L \&/ α})#λ]
    def applicative[L: Semigroup] = Applicative[({type λ[α] = L \&/ α})#λ]
    def monad[L: Semigroup] = Monad[({type λ[α] = L \&/ α})#λ]
    def semigroup[L: Semigroup, R: Semigroup] = Semigroup[L \&/ R]
    def cobind[L] = Cobind[({type λ[α] = L \&/ α})#λ]
    def foldable[L] = Foldable[({type λ[α] = L \&/ α})#λ]
    def traverse[L] = Traverse[({type λ[α] = L \&/ α})#λ]
    def band[L: Band, R: Band] = Band[L \&/ R]
    def bifunctor = Bifunctor[\&/]
    def bifoldable = Bifoldable[\&/]
    def bitraverse = Bitraverse[\&/]

    // checking absence of ambiguity
    def functor[L: Semigroup] = Functor[({type λ[α] = L \&/ α})#λ]
    def equal[A: Order, B: Order] = Equal[A \&/ B]
    def semigroup[L: Band, R: Band] = Semigroup[L \&/ R]
  }
}
