import { Directive, ElementRef, Renderer2, OnInit, OnDestroy } from '@angular/core';

// Diretiva que anima um elemento quando ele entra na viewport
// Uso no HTML: <div appAnimateOnScroll></div>
@Directive({
  selector: '[appAnimateOnScroll]'
})

/**
 * Diretiva AnimateOnScroll
 *
 * Aplica uma animação a um elemento quando ele entra na viewport ao fazer scroll.
 * A animação é disparada apenas uma vez por elemento.
 */
export class AnimateOnScrollDirective implements OnInit, OnDestroy {

  /**
   * Observer que monitora a interseção do elemento com a viewport
   * usando a API IntersectionObserver
   */
  private observer!: IntersectionObserver;

  /**
   * Flag que garante que a animação seja executada apenas uma vez
   */
  private hasAnimated = false;

  /**
   * @param el - referência ao elemento DOM ao qual a diretiva está ligada
   * @param renderer - serviço do Angular para manipulação segura do DOM
   */
  constructor(private el: ElementRef, private renderer: Renderer2) {}

  /**
   * Método do ciclo de vida Angular chamado após a diretiva ser inicializada
   */
  ngOnInit() {
    // Inicialmente, adiciona a classe 'hidden' para manter o elemento invisível
    this.renderer.addClass(this.el.nativeElement, 'hidden');

    // Configura o IntersectionObserver para observar o elemento
    this.observer = new IntersectionObserver(
      (entries) => {
        // Itera sobre todas as entradas observadas
        entries.forEach(entry => {
          // Se o elemento estiver visível e ainda não tiver animado
          if (entry.isIntersecting && !this.hasAnimated) {
            // Adiciona a classe 'show' para disparar a animação
            this.renderer.addClass(this.el.nativeElement, 'show');

            // Marca que o elemento já animou, para não repetir
            this.hasAnimated = true;

            // Para de observar o elemento, já que a animação ocorreu
            this.observer.unobserve(this.el.nativeElement);
          }
        });
      },
      {
        // Dispara quando 30% do elemento estiver visível
        threshold: 0.3,
        // Sem margem extra em relação à viewport
        rootMargin: '0px 0px 0px 0px'
      }
    );

    // Inicia a observação do elemento
    this.observer.observe(this.el.nativeElement);
  }

  /**
   * Método do ciclo de vida Angular chamado antes da diretiva ser destruída
   * Garante que o observer seja desconectado para evitar memory leaks
   */
  ngOnDestroy() {
    if (this.observer) this.observer.disconnect();
  }
}
